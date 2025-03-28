import pandas as pd
import os
import mysql.connector
from dotenv import load_dotenv
from sklearn.metrics.pairwise import cosine_similarity
from sqlalchemy import create_engine

load_dotenv()

config = {
    "user": "root",
    "password": os.getenv("ROOT_PWD"),
    "host": os.getenv("HOST"),
    "database": os.getenv("DATABASE"),
}
engine = create_engine(
    f'mysql+mysqlconnector://{config["user"]}:{config["password"]}@{config["host"]}/{config["database"]}'
)
connection = engine.connect()
mysqlconn = mysql.connector.connect(**config)

transactions = pd.read_sql("SELECT * FROM transactions", connection)
transaction_items = pd.read_sql("SELECT * FROM transaction_products", connection)
products = pd.read_sql("SELECT * FROM products", connection)


transaction_items = transaction_items.merge(
    transactions[["id", "buyer_id"]], left_on="transaction_id", right_on="id"
)

user_product_matrix = (
    transaction_items.groupby(["buyer_id", "product_id"])
    .size()
    .reset_index(name="purchase_count")
)

user_product_matrix = user_product_matrix.pivot_table(
    index="buyer_id", columns="product_id", values="purchase_count", fill_value=0
)

product_similarity_matrix = cosine_similarity(user_product_matrix.T)

product_similarity_df = pd.DataFrame(
    product_similarity_matrix,
    index=user_product_matrix.columns,
    columns=user_product_matrix.columns,
)


def get_recommendations(product_id, top_n=5):
    similar_products = (
        product_similarity_df[product_id].drop(product_id).sort_values(ascending=False)
    )

    return similar_products.head(top_n)


recommendations = []
for product_id in user_product_matrix.columns:
    top_recommendations = get_recommendations(product_id)

    for recommended_product_id, _ in top_recommendations.items():
        recommendations.append(
            {
                "product_id": product_id,
                "recommended_product_id": recommended_product_id,
            }
        )

recommendations_df = pd.DataFrame(recommendations)

# SQL Query for inserting recommendations into the database
insert_query = """
    INSERT INTO recommendations (product_id, recommended_product_id)
    VALUES (%s, %s)
"""
cursor = mysqlconn.cursor()
# Iterate over DataFrame rows and insert each record into the database
for index, row in recommendations_df.iterrows():
    cursor.execute(
        insert_query, (int(row["product_id"]), int(row["recommended_product_id"]))
    )


mysqlconn.commit()
connection.close()
