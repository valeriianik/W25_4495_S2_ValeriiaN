import sqlalchemy as sa
from sqlalchemy.orm import DeclarativeBase, sessionmaker, relationship
from sqlalchemy import Column, Integer, ForeignKey
from dotenv import load_dotenv
import os


load_dotenv()

USER = "root"
HOST = os.getenv("HOST")
DB = os.getenv("DATABASE")
PWD = os.getenv("ROOT_PWD")
PORT = os.getenv("PORT")
CONNECTION_STR = f"mysql+mysqlconnector://{USER}:{PWD}@{HOST}:{PORT}/{DB}"
mysqlengine = sa.create_engine(CONNECTION_STR, echo=True)

Session = sessionmaker(bind=mysqlengine)
session = Session()


metadata = sa.MetaData()
metadata.reflect(bind=mysqlengine)

# Define tables
users_table = sa.Table("users", metadata, autoload_with=mysqlengine)
transaction_table = sa.Table("transactions", metadata, autoload_with=mysqlengine)
products_table = sa.Table("products", metadata, autoload_with=mysqlengine)
orders_table = sa.Table("orders", metadata, autoload_with=mysqlengine)
reccomended_products_table = sa.Table(
    "recommendations", metadata, autoload_with=mysqlengine
)


class Base(DeclarativeBase):
    pass


class User(Base):
    __table__ = users_table


class Transaction(Base):
    __table__ = transaction_table


class Product(Base):
    __table__ = products_table


class Order(Base):
    __table__ = orders_table


class ProductRecommendation(Base):
    __table__ = reccomended_products_table
