import pandas as pd
import numpy as np
from db.alchemy import (
    ProductRecommendation,
    session,
    products_table,
    mysqlengine,
    reccomended_products_table,
)
from sklearn.metrics.pairwise import cosine_similarity
from sklearn.preprocessing import MinMaxScaler


# Load products
def load_products_df():
    with mysqlengine.connect() as connection:
        query = products_table.select()
        df = pd.read_sql(query, connection)
        return df


def get_recommendations_for_product(products_df, similarity_matrix, product_id, n=2):
    product_idx = products_df.index[products_df["id"] == product_id].tolist()[0]

    similarity_scores = similarity_matrix[product_idx]

    similar_indices = similarity_scores.argsort()[::-1][1 : n + 1]

    recommendations = products_df.iloc[similar_indices][["id", "category", "price"]]
    return recommendations


def create_reccomendations(products_df):
    category_encoded = pd.get_dummies(products_df["category"])

    scaler = MinMaxScaler()
    price_normalized = scaler.fit_transform(products_df[["price"]])

    feature_matrix = np.hstack((category_encoded.values, price_normalized))
    similarity_matrix = cosine_similarity(feature_matrix)

    insert_data = []
    for product_idx, product_id in enumerate(products_df["id"]):
        similarity_scores = similarity_matrix[product_idx]
        similar_indices = similarity_scores.argsort()[::-1][1:6]
        for idx in similar_indices:
            insert_data.append(
                {
                    "product_id": product_id,
                    "recommended_product_id": int(products_df.iloc[idx]["id"]),
                }
            )
    with mysqlengine.connect() as connection:
        connection.execute(reccomended_products_table.insert(), insert_data)
        connection.commit()


def clear_reccomendations():
    session.query(ProductRecommendation).delete()
    session.commit()


def main():
    clear_reccomendations()
    products_df = load_products_df()
    create_reccomendations(products_df)


if __name__ == "__main__":
    main()
