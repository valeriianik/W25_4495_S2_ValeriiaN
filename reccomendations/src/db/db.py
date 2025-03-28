import time
import os
from typing import Optional
import mysql.connector
from dotenv import load_dotenv
from mysql.connector.abstracts import MySQLConnectionAbstract
from mysql.connector.pooling import PooledMySQLConnection

load_dotenv()

config = {
    "user": "root",
    "password": os.getenv("ROOT_PWD"),
    "host": os.getenv("HOST"),
    "database": os.getenv("DATABASE"),
}

# Retry connection with timeout
max_retries = 10
retry_delay = 2  # seconds
db: Optional[PooledMySQLConnection | MySQLConnectionAbstract] = None
for attempt in range(max_retries):
    try:
        db = mysql.connector.connect(**config)
        print("Successfully connected to MySQL!")
        break
    except mysql.connector.Error as err:
        print(f"Attempt {attempt + 1} failed: {err}")
        if attempt < max_retries - 1:
            time.sleep(retry_delay)
        else:
            print("Failed to connect after all retries.")
            raise

connection = db
