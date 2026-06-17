Jackpot Bets API
-----------------

Spring Boot REST API with Apache Kafka integration and H2 in-memory database.

Tech Stack
------------

1. Java 17
2. Spring Boot 3.2.5
3. Apache Kafka (Kafka version: 3.6.2)
4. H2 Database in-memory
5. Maven

Prerequisites
---------------
  
To run and test this application, you need:

1. Java 17+: Check with java -version
2. Apache Kafka (kafka_2.13-4.1.1) - Kafka Raft mode  - NO ZOOKEEPER:
   Required because endpoints publish to and consume from Kafka topic jackpot-bets
3. Postman or any API client for testing

How to Run
----------

Step 1: Start Kafka


Download Kafka from kafka.apache.org

Run the following commands with BASH:

cd C:\kafka\kafka_2.13-4.1.1

bin\windows\kafka-storage.bat format -t %RANDOM% -c config\server.properties

bin\windows\kafka-server-start.bat config\server.properties

Kafka runs on localhost:9092. 

Topic jackpot-bets will be auto-created.


Step 2: Run the Application


Download jackpot-service-0.0.1-SNAPSHOT.jar from Releases.

Run the following with BASH:

   java -jar jackpot-service-0.0.1-SNAPSHOT.jar

Application starts on http://localhost:8082


Console output: 

Started Application in X seconds... Netty started on port 8082


Step 3: Test with Postman / Bruno


API Endpoints
----------------

1. You run this endpoint to see initial data in the database tables. After every bet you can come here and run again to see data from different tables.
   Initially, on startup,dtata is loaded in the JACKPOT table via data.sql file.

GET  http://localhost:8082/jackpot-service/data/all

   Response:

   [
  {
    "row": {
      "ID": 1,
      "NAME": "Fixed Reward Jackpot - Winner",
      "INITIAL_POOL": 1000.00,
      "CURRENT_POOL": 6000.00,
      "CONTRIBUTION_STRATEGY": "FIXED",
      "REWARD_STRATEGY": "FIXED",
      "CONTRIBUTION_PERCENTAGE": 0.05,
      "CREATED_AT": "2026-06-17T08:51:58.213649",
      "UPDATED_AT": "2026-06-17T08:51:58.213649"
    },
    "table": "JACKPOT"
  },
  {
    "row": {
      "ID": 2,
      "NAME": "Fixed Reward Jackpot - No Winner",
      "INITIAL_POOL": 1000.00,
      "CURRENT_POOL": 4000.00,
      "CONTRIBUTION_STRATEGY": "FIXED",
      "REWARD_STRATEGY": "FIXED",
      "CONTRIBUTION_PERCENTAGE": 0.05,
      "CREATED_AT": "2026-06-17T08:51:58.215651",
      "UPDATED_AT": "2026-06-17T08:51:58.215651"
    },
    "table": "JACKPOT"
  },
  {
    "row": {
      "ID": 3,
      "NAME": "Variable Reward Jackpot - Winner",
      "INITIAL_POOL": 1000.00,
      "CURRENT_POOL": 7000.00,
      "CONTRIBUTION_STRATEGY": "VARIABLE",
      "REWARD_STRATEGY": "VARIABLE",
      "CONTRIBUTION_PERCENTAGE": 0.10,
      "CREATED_AT": "2026-06-17T08:51:58.216653",
      "UPDATED_AT": "2026-06-17T08:51:58.216653"
    },
    "table": "JACKPOT"
  },
  {
    "row": {
      "ID": 4,
      "NAME": "Variable Reward Jackpot - No Winner",
      "INITIAL_POOL": 1000.00,
      "CURRENT_POOL": 7500.00,
      "CONTRIBUTION_STRATEGY": "VARIABLE",
      "REWARD_STRATEGY": "VARIABLE",
      "CONTRIBUTION_PERCENTAGE": 0.10,
      "CREATED_AT": "2026-06-17T08:51:58.216653",
      "UPDATED_AT": "2026-06-17T08:51:58.216653"
    },
    "table": "JACKPOT"
  }
]

After placing a bet, you can run this endpoint again to see data from other tables e.g. bet table, etc.


2. Publish Bet to Kafka


POST  http://localhost:8082/jackpot-service/bets

Request Body:
{
  "userId": 101,
  "jackpotId": 1,
  "amount": 100.00
}

Success Response: 200 OK

Bet published with ID: 1


3. Execute this endpoint to see if the bet won or not:

   http://localhost:8082/jackpot-service/bets/1/evaluate

   Response:

   {
	  "percentageUsed": 20.00,
	  "winner": true,
	  "jackpotId": 1,
	  "betId": 1,
	  "rewardAmount": 1202.00,
	  "strategyType": "FIXED"
	}  

Another run:
----------------

Request:
{
  "userId": 102,
  "jackpotId": 2,
  "amount": 200.00
}


Success Response: 200 OK

Bet published with ID: 2

Did the bet win?
Run: 

GET   http://localhost:8082/jackpot-service/bets/2/evaluate

Response:
{
  "winner": false,
  "message": "No reward generated for bet id: 2",
  "jackpotId": 2,
  "betId": 2
}

Another run:
---------------

Request:

{
  "userId": 103,
  "jackpotId": 3,
  "amount": 150.00
}


Success Response: 200 OK

Bet published with ID: 3

Did the bet win?
Run: 

GET   http://localhost:8082/jackpot-service/bets/3/evaluate

Response:
{
  "winner": false,
  "message": "No reward generated for bet id: 3",
  "jackpotId": 3,
  "betId": 3
}

Another run:
-------------

Request:

{
  "userId": 103,
  "jackpotId": 3,
  "amount": 300.00
}


Success Response: 200 OK

Bet published with ID: 4

Did the bet win?
Run: 

GET   http://localhost:8082/jackpot-service/bets/4/evaluate

Response:
{
  "percentageUsed": 10.00,
  "winner": true,
  "jackpotId": 3,
  "betId": 4,
  "rewardAmount": 710.50,
  "strategyType": "VARIABLE"
}

Another run:
-----------

I'll check whether or not the is placed is winning I'll supply a bet ID that does not exist:

Did the bet win?
Run: 

GET   http://localhost:8082/jackpot-service/bets/14/evaluate

Response:

{
  "winner": false,
  "betId": 14,
  "message": "Unexpected error: Bet not found with id: 14"
}

