# fetch-backend-exercise

### created by Branan Smith

This is a backend exercise for Fetch, where I used Java Spring boot to allow users to input a receipt in the form of a JSON file. After a receipt is submitted, an id is returned. For each receipt, points are accumulated based on certain criteria, and using this id, you can view the points for that particular receipt.

## How to use

Download or clone this repository.

Navigate to receipts > src/main/java > ReceiptsApplication and run the application.
Here you should see a "Tomcat started on port {port number}." Using that port number, go to your local web browser
and type in localhost:{port number}/receipts/process. 

In my case the port number is 8080, so I would type into the search browser:
> localhost:8080/receipts/process

From here you should see an option to upload a file, click on the "Choose File" button and select your receipt.
After your receipt is selected, click on the "Upload" button. Here it should return an id.

Using that id, go to localhost:{your port number}/receipts/{the id you just received}/points

Here you should see the number of points associated with the receipt you uploaded.
