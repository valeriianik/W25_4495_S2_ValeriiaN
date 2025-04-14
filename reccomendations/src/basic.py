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

# Origial code for seed data
# def create_reccomendations(products_df):
#     category_encoded = pd.get_dummies(products_df["category"])
#
#     scaler = MinMaxScaler()
#     price_normalized = scaler.fit_transform(products_df[["price"]])
#
#     feature_matrix = np.hstack((category_encoded.values, price_normalized))
#     similarity_matrix = cosine_similarity(feature_matrix)
#
#     insert_data = []
#     for product_idx, product_id in enumerate(products_df["id"]):
#         similarity_scores = similarity_matrix[product_idx]
#         similar_indices = similarity_scores.argsort()[::-1][1:6]
#         for idx in similar_indices:
#             insert_data.append(
#                 {
#                     "product_id": product_id,
#                     "recommended_product_id": int(products_df.iloc[idx]["id"]),
#                 }
#             )
#     with mysqlengine.connect() as connection:
#         connection.execute(reccomended_products_table.insert(), insert_data)
#         connection.commit()

# Code for inserted data
# def create_reccomendations(products_df):
#     insert_data = []
#
#     for category in products_df["category"].unique():
#         subset_df = products_df[products_df["category"] == category].reset_index(drop=True)
#
#         if len(subset_df) < 2:
#             continue
#
#         # ✅ Define scaler inside the function
#         scaler = MinMaxScaler()
#         price_normalized = scaler.fit_transform(subset_df[["price"]])
#         feature_matrix = price_normalized
#
#         similarity_matrix = cosine_similarity(feature_matrix)
#
#         for product_idx, product_id in enumerate(subset_df["id"]):
#             similarity_scores = similarity_matrix[product_idx]
#             similar_indices = similarity_scores.argsort()[::-1][1:6]
#
#             for idx in similar_indices:
#                 insert_data.append({
#                     "product_id": product_id,
#                     "recommended_product_id": int(subset_df.iloc[idx]["id"]),
#                 })
#
#     with mysqlengine.connect() as connection:
#         connection.execute(reccomended_products_table.insert(), insert_data)
#         connection.commit()

def create_recommendations(products_df):
    insert_data = []

    # Group by category
    for category in products_df["category"].unique():
        subset_df = products_df[products_df["category"] == category].reset_index(drop=True)

        # Skip categories with less than 2 products
        if len(subset_df) < 2:
            continue

        # Normalize price (only use price because category is already the same)
        scaler = MinMaxScaler()
        price_normalized = scaler.fit_transform(subset_df[["price"]])
        similarity_matrix = cosine_similarity(price_normalized)

        for product_idx, product_id in enumerate(subset_df["id"]):
            similarity_scores = similarity_matrix[product_idx]

            # Get top 5 most similar products (excluding itself)
            similar_indices = similarity_scores.argsort()[::-1]
            similar_indices = [i for i in similar_indices if i != product_idx][:5]

            for idx in similar_indices:
                insert_data.append({
                    "product_id": product_id,
                    "recommended_product_id": int(subset_df.iloc[idx]["id"]),
                })

    # Save to database
    with mysqlengine.connect() as connection:
        connection.execute(reccomended_products_table.insert(), insert_data)
        connection.commit()


def clear_reccomendations():
    session.query(ProductRecommendation).delete()
    session.commit()


# def main():
#     clear_reccomendations()
#     products_df = load_products_df()
# #     create_reccomendations(products_df)
# create_recommendations(products_df)

def main():
    clear_reccomendations()
    products_df = load_products_df()  # ✅ First load the product data
    create_recommendations(products_df)  # ✅ Then pass it into the function

if __name__ == "__main__":
    main()
