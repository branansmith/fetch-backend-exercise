# fetch-backend-exercise

### created by Branan Smith

This is a backend exercise for Fetch, where I used Java Spring boot to allow users to input a receipt in the form of a JSON file. After a receipt is submitted, an id is returned. For each receipt, points are accumulated based on certain criteria, and using the id, you can view the points for that particular receipt.

## How to use

Download or clone this repository.

```bash
   git clone https://github.com/branansmith/fetch-backend-exercise.git
   cd fetch-backend-exercise
```
Build the docker image
```bash
    docker build -t receipts-app .
```
Run the Docker container
```bash
  docker run -p 8080:8080 receipts-app
```
Enter into your search bar

> localhost:8080/receipts/process

To access the upload page. Here you are given the option to upload a file. 

Upload one of the two receipt files found in the receipts folder and click "Upload".

You should receive an id, which you can use to view the points by going to:

> localhost:8080/receipts/{id you received}/points

## Potential Errors

There is a chance that the docker container will fail to load the metadata for the openjdk. To solve this issue, make sure you are logged into your docker account using the terminal command:

```bash
   docker login -u {your username}
```
