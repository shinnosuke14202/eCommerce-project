from sklearn.metrics.pairwise import cosine_similarity
from sklearn.feature_extraction.text import CountVectorizer
import pandas as pd
from helper import helper as f
import numpy as np
import pickle

books = pd.read_csv("./data/products.csv")
holidays = pd.read_csv("./data/holidays.csv")

# handle books data
book_data = books[['description', 'categories', 'title', 'id']]
books = books[['description', 'categories', 'title']]

books['description'] = books['description'].apply(
    lambda x: f.handle_unwanted(x))
books['description'] = books['description'].apply(lambda x: f.to_list(x))

books['categories'] = books['categories'].apply(
    lambda x: f.handle_categories(x))

books['title'] = books['title'].apply(lambda x: f.handle_title(x))

new_bdf = books[['description', 'categories', 'title']].copy()

new_bdf['tags'] = new_bdf['description'] + \
    new_bdf['categories'] + new_bdf['title']
new_bdf['tags'] = new_bdf['tags'].apply(lambda x: f.hanlde_tags(x))

# handle holidays data

holidays['description'] = holidays['description'].apply(
    lambda x: f.handle_unwanted(x))
holidays['description'] = holidays['description'].apply(
    lambda x: x.strip().lower())

# use algorythm
combined_df = pd.DataFrame()
combined_df['tags'] = pd.concat(
    [holidays['description'], new_bdf['tags']], axis=0)
combined_df.reset_index(drop=True, inplace=True)

cv = CountVectorizer(max_features=5000, stop_words=f.vietnamese_stopwords)
vector = cv.fit_transform(combined_df['tags']).toarray()

similarity = cosine_similarity(vector)[0:holidays['description'].size]

def recommend_books_base_on_holiday(date):
    holidays_size = holidays['description'].size
    holiday_index = holidays[holidays['date'] == date].index[0]
    distances = sorted(list(enumerate(similarity[holiday_index]))[holidays_size:], key=lambda x: x[1], reverse=True)[0:4]

    print(distances)

    for distance in distances:
        index = distance[0] - holidays_size
        book = book_data.iloc[index]
        print(book.title)

pickle.dump(holidays, open("pickle_files/holidays.pkl", 'wb'))
pickle.dump(book_data, open("pickle_files/book_data.pkl", 'wb'))
pickle.dump(similarity, open("pickle_files/similarity_holidays.pkl", 'wb'))

recommend_books_base_on_holiday("14-02")
