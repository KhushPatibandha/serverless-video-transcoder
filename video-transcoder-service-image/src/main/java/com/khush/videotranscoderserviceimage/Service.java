package com.khush.videotranscoderserviceimage;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.transfer.Transfer;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.TransferManagerBuilder;

public class Service {
    private static String objectKey = System.getenv("OBJECT_KEY");
    private static String permBucketName = System.getenv("PERM_BUCKET_NAME");
    private static String getUrl = System.getenv("GET_URL");
    private static String accessKey = System.getenv("ACCESS_KEY");
    private static String secretAccessKey = System.getenv("SECRET_ACCESS_KEY");
    private static String region = System.getenv("REGION");

    private static final BasicAWSCredentials creds = new BasicAWSCredentials(accessKey, secretAccessKey);
    private static final Regions regions = Regions.fromName(region);
    private static final AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
                                            .withCredentials(new AWSStaticCredentialsProvider(creds))
                                            .withRegion(regions)
                                            .build();

    public static void main(String[] args) {
        downloadVideoFile();
        String resolution = getVideoResolution();
        transcodeVideo(resolution);
        uploadFolder();
    }

    private static void transcodeVideo(String resolution) {
        String[] allResolutions = {"3840x2160", "2560x1440", "1920x1080", "1280x720", "854x480", "640x360", "426x240", "256x144"};
        int originalIndex = Arrays.asList(allResolutions).indexOf(resolution.replace("*", "x"));
        for(int i = originalIndex + 1; i < allResolutions.length; i++) {
            try {
                String filePath = "./downloads/" + objectKey;
                String outputPath = "./downloads/transcoded/" + objectKey.split("\\.")[0] + "/";
                Path path = Paths.get(outputPath);
                if (!Files.exists(path)) {
                    Files.createDirectories(path);
                }
                ProcessBuilder processBuilder = new ProcessBuilder("ffmpeg", "-i", filePath, "-s", allResolutions[i], outputPath + allResolutions[i] + ".mp4");
                Process process = processBuilder.start();

                // Read the output of the process in a separate thread
                new Thread(() -> {
                    try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                        String line;
                        while ((line = reader.readLine()) != null) {
                            System.out.println(line);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }).start();

                // Read the error output of the process in a separate thread
                new Thread(() -> {
                    try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {
                        String line;
                        while ((line = reader.readLine()) != null) {
                            System.err.println(line);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }).start();

                int exitCode = process.waitFor();
                if (exitCode != 0) {
                    System.out.println("Error occurred while transcoding video. Exit code: " + exitCode);
                }
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


    private static void downloadVideoFile() {
        try {
            URL url = new URL(getUrl);
            InputStream in = url.openStream();
            Path outputPath = Paths.get("downloads/" + objectKey);
            Files.createDirectories(outputPath.getParent());
            Files.copy(in, outputPath, StandardCopyOption.REPLACE_EXISTING);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String getVideoResolution() {
        try {
            ProcessBuilder processBuilder = new ProcessBuilder("ffmpeg", "-i", "./downloads/"+ objectKey);
            processBuilder.redirectErrorStream(true);
            Process process = processBuilder.start();

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                String resolutionPattern = "(\\d+x\\d+)(?=[ \\[])";
                Pattern pattern = Pattern.compile(resolutionPattern);
    
                while ((line = reader.readLine()) != null) {
                    Matcher matcher = pattern.matcher(line);
                    if (matcher.find()) {
                        String resolution = matcher.group();
                        return resolution;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;

    }

    private static void uploadFolder() {
        TransferManager tm = TransferManagerBuilder.standard().withS3Client(s3Client).build();
        String dirPath = "./downloads/transcoded/";
        String folderName = "transcoded/";
        boolean recursive = true;
        Transfer upload = tm.uploadDirectory(permBucketName, folderName, new File(dirPath), recursive);

        try {
            upload.waitForCompletion();
        } catch (AmazonServiceException e) {
            System.err.println("Amazon service error: "+ e.getMessage());
            System.exit(1);
        } catch (AmazonClientException e) {
            System.err.println("Amazon client error: "+ e.getMessage());
            System.exit(1);
        } catch (InterruptedException e) {
            System.err.println("Upload interrupted: "+ e.getMessage());
            System.exit(1);
        }
        tm.shutdownNow();
    }
}
