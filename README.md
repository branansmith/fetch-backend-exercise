# fetch-backend-exercise

### created by Branan Smith

This is a backend exercise for Fetch, where I used Java Spring boot to allow users to input a receipt in the form of a JSON file. After a receipt is submitted, an id is returned. For each receipt, points are accumulated based on certain criteria, and using this id, you can view the points for that particular receipt.

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
