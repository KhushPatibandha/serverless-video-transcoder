
# Serverless Video Transcoder

A completely serverless java based video transcoder, this will help you transcoder or downscale videos very quickly.

Ever wondered why a video is stuck in processing while we upload on youtube?

The answer is that it is getting transcoded into various formats.

I have also build something like that for myself and for y'all.

Also the frontend client is not available, so use any frontend client like Postman, etc of your choice.


## Run Locally

Clone the project

```bash
  git clone https://github.com/KhushPatibandha/serverless-video-transcoder.git
```

Go to the project directory

```bash
  cd .\serverless-video-transcoder\
```


## Environment Variables

To run this project, you will need to add the following environment variables to your .env file, make sure you have your .env file under the \video-transcoder\src\main\resources\\.env . 

An example on how to set it up is shown below. 

Also the temp bucket name and perm bucket name should be unique globally and hence make a name that is unique to you and you only globally. For precaution write random numbers after the bucket name

`AWS_ACCOUNT_ID`=123456789123

`USER_ACCESS_KEY`=AQAZ53MPON7OBSZI6GWQ

`USER_SECRET_ACCESS_KEY`=kDqopnfhke1E3MOdMunidhILBmRGR+ncKTkhqndhMr

`AWS_REGION`=ap-south-1

`TEMP_BUCKET_NAME`=khush-random-12012-temp

`PERM_BUCKET_NAME`=khush-random-12012-perm

`EMAIL_ADDRESS`=your@emailaddress@gmail.com
## API Reference - Quick Start

In this section we will talk about how to get started with this application in under 2 minutes. To dig even deeper into the apis check out the API Documentation below(Not added as of 28-01-2024).

#### Get the email set-up

```http
  POST /api/ses/create-identity
```
Check your emails for the conformation mail. Only after accepting the request, proceed further.

Should be done only once when running the application for the first time.

#### Create Resources

```http
  POST /api/quickstart/create-resources
```
might take some time to set everything up since we are creating all the resources in one go, also dependence on the internet connection.

Should be done only once when running the application for the first time.

#### Get the url to post videos
```http
  GET /api/s3/get/put-url
```
This will return you a url that you can use to send any video to aws for transcoding.

#### Sending the PUT url
```http
  PUT /Url-that-we-got-from-above
```
with this you can post any videos by going to the "Body > Binary > Select file" in you frontend client. That's it, just click send.

#### updating the .jar file everytime you pull from main branch
```http
  PUT /api/lambda/update/code
```
this will update your .jar file in the lambda function with the latest code, so everytime you pull from main branch to local environment mack sure you first run this api for latest changes to the code base.