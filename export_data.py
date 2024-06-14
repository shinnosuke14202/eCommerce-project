import pymysql
import pandas as pd

# Connect to MySQL database
connection = pymysql.connect(host='localhost',
                             port=3306,
                             user='root',
                             password='14202',
                             database='ecom_app_jpa')

# SQL query to select data
sql_query = (
    "SELECT p.id, p.author, p.description, p.price, p.title, GROUP_CONCAT(c.category_name SEPARATOR ', ') AS categories "
    "FROM product p "
    "INNER JOIN product_categories pc ON p.id = pc.product_id "
    "INNER JOIN categories c ON pc.categories_id = c.id "
    "GROUP BY p.author, p.description, p.price, p.title, p.id"
)

# Execute query and load data into a DataFrame
df = pd.read_sql(sql_query, connection)

# Close database connection
connection.close()

# Save DataFrame to CSV
df.to_csv('./data/products.csv', index=False)

