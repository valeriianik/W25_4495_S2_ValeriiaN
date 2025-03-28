from faker import Faker
from .db import connection
import random

faker = Faker("en_US")
cursor = connection.cursor()


NUM_USERS = 50
NUM_ENTRE = 10
NUM_PRODS = 200
NUM_ORDERS = 5000


def seed_users():
    user_types = ["BUYER", "SELLER", "ENTREPRENEUR"]
    users = []

    for _ in range(NUM_USERS):
        email = faker.unique.email()
        password = faker.password(length=12)  # Generate a random password
        user_type = random.choice(user_types)
        # Insert query
        query = """
            INSERT INTO users (email, password, user_type)
            VALUES (%s,%s, %s)
        """
        cursor.execute(query, (email, password, user_type))
        user_id = cursor.lastrowid  # Get the auto-generated ID
        users.append((user_id, email, password, user_type))
    return users


# Generate Entrepreneur Users
def seed_entrepreneur_users(users):
    identity_types = ["passport", "driver_license", "national_id"]
    entrepreneurs = []

    # Use only users with type "ENTREPRENEUR"
    entrepreneur_users = [u for u in users if u[3] == "ENTREPRENEUR"]

    for i, user in enumerate(entrepreneur_users):
        user_id, _, _, _ = user
        about = faker.text(max_nb_chars=200)
        first_name = faker.first_name()
        identity_type = random.choice(identity_types)
        last_name = faker.last_name()
        photo_path = f"/photos/entrepreneur_{i}.jpg" if random.random() > 0.2 else None

        query = """
            INSERT INTO entrepreneur_users (about, first_name, identity_type, last_name, photo_path, user_id)
            VALUES (%s, %s, %s, %s, %s, %s)
        """
        cursor.execute(
            query, (about, first_name, identity_type, last_name, photo_path, user_id)
        )
        entrepreneur_id = cursor.lastrowid  # Capture the generated ID
        entrepreneurs.append(
            (
                entrepreneur_id,
                about,
                first_name,
                identity_type,
                last_name,
                photo_path,
                user_id,
            )
        )
    return entrepreneurs


# Generate Products
def seed_products(entrepreneur_ids):
    categories = ["Electronics", "Clothing", "Home", "Books", "Sports"]
    products = []

    for i in range(NUM_PRODS):

        category = random.choice(categories)
        image_path = f"/images/product_{i}.jpg"
        posted_date = faker.date_time_between(start_date="-1y", end_date="now")
        price = round(random.uniform(5.0, 500.0), 2)
        product_description = faker.text(max_nb_chars=200)
        product_name = faker.word().capitalize() + " " + faker.word().capitalize()
        quantity = random.randint(1, 100)
        entrepreneur_id = random.choice(entrepreneur_ids)

        query = """
            INSERT INTO products (category, image_path, posted_date, price,
                                product_description, product_name, quantity, entrepreneur_id)
            VALUES (%s,%s, %s, %s,%s, %s, %s, %s)
        """
        cursor.execute(
            query,
            (
                category,
                image_path,
                posted_date,
                price,
                product_description,
                product_name,
                quantity,
                entrepreneur_id,
            ),
        )
        product_id = cursor.lastrowid  # Capture the generated ID
        products.append(
            (
                product_id,
                category,
                image_path,
                posted_date,
                price,
                product_description,
                product_name,
                quantity,
                entrepreneur_id,
            )
        )

    return products


# Generate Transactions
def seed_transactions(user_ids, product_ids):
    statuses = ["pending", "completed", "shipped", "cancelled"]
    transactions = []

    for _ in range(NUM_ORDERS):
        city = faker.city()
        # country = faker.country()
        country = "United States"
        phone_number = faker.phone_number()
        postal_code = faker.postcode()
        province = faker.state()
        quantity = random.randint(1, 10)
        status = random.choice(statuses)
        street_address = faker.street_address()
        product_id = random.choice(product_ids)
        total_price = round(random.uniform(10.0, 1000.0), 2)
        transaction_date = faker.date_time_between(start_date="-2y", end_date="now")
        buyer_id = random.choice(user_ids)
        seller_id = random.choice(user_ids)

        # Ensure buyer_id and seller_id are different
        while seller_id == buyer_id:
            seller_id = random.choice(user_ids)

        transactions.append(
            (
                city,
                country,
                phone_number,
                postal_code,
                province,
                quantity,
                status,
                street_address,
                total_price,
                transaction_date,
                buyer_id,
                product_id,
                seller_id,
            )
        )

        query = """
            INSERT INTO transactions (city, country, phone_number, postal_code, province,
                                   quantity, status, street_address, total_price, transaction_date,
                                   buyer_id, product_id, seller_id)
            VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s)
        """
        cursor.execute(
            query,
            (
                city,
                country,
                phone_number,
                postal_code,
                province,
                quantity,
                status,
                street_address,
                total_price,
                transaction_date,
                buyer_id,
                product_id,
                seller_id,
            ),
        )

    return transactions


# Execute seeding
try:
    cursor.execute("SET FOREIGN_KEY_CHECKS = 0;")  # Disable foreign key checks
    cursor.execute("DELETE FROM transactions;")
    cursor.execute("DELETE FROM products;")
    cursor.execute("DELETE FROM entrepreneur_users;")
    cursor.execute("DELETE FROM users;")
    cursor.execute("SET FOREIGN_KEY_CHECKS = 1;")  # Re-enable foreign key checks

    # Seed users first
    users = seed_users()
    user_ids = [user[0] for user in users]
    # Seed entrepreneur_users (needs user_ids)
    entrepreneurs = seed_entrepreneur_users(users)
    entrepreneur_ids = [entrepreneur[0] for entrepreneur in entrepreneurs]

    # Seed products (needs entrepreneur_ids)
    products = seed_products(entrepreneur_ids)
    product_ids = [product[0] for product in products]

    # Seed transactions (needs user_ids and product_ids)
    transactions = seed_transactions(user_ids, product_ids)

    # Commit all changes
    connection.commit()
    print(
        f"Successfully seeded: {NUM_USERS} users, {NUM_PRODS} products, {NUM_USERS} transactions"
    )

except Exception as e:
    print(f"Error occurred: {str(e)}")
    connection.rollback()

finally:
    cursor.close()
    connection.close()
