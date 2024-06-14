from sklearn.metrics.pairwise import cosine_similarity
from sklearn.feature_extraction.text import CountVectorizer
from underthesea import word_tokenize
import pandas as pd
import numpy as np
import pickle

books = pd.read_csv("./data/products.csv")

def remove_space(text):
    return ' '.join(text.split())

def join_words(text):
    return text.replace(" ", "")

def remove_quotation(text):
    return text.replace('"', '')

def convert_to_list(text):
    output_list = [word.strip() for word in text.split(',')]
    return output_list

def join_word_list(text):
    output_list = [word.strip().replace(" ", "") for word in text]
    return output_list

def handle_description(text):
    return text.replace(".", " ").replace(",", " ").split()

# remove unnecessary space
books['author'] = books['author'].apply(lambda x: remove_space(x))
books['description'] = books['description'].apply(lambda x: remove_space(x))
books['title'] = books['title'].apply(lambda x: remove_space(x))

# author name, category if separate don't have meaning
# nguyennhatanh -> nguyen, nhat, anh
books['author'] = books['author'].apply(lambda x: join_words(x))
# books['categories'] = books['categories'].apply(lambda x: join_words(x))

# remove quotation
books['description'] = books['description'].apply(lambda x: remove_quotation(x))

books['description'] = books['description'].apply(lambda x: handle_description(x))

# # change sentences into group of meaning words
# books['description'] = books['description'].apply(lambda x: word_tokenize(x))
# books['description'] = books['description'].apply(lambda x: join_word_list(x))

# convert string to list
books['categories'] = books['categories'].apply(convert_to_list)
books['author'] = books['author'].apply(convert_to_list)
books['price'] = books['price'].apply(lambda x: [str(x)])

# create new column tag have other columns words
books['tags'] = books['author'] + books['description'] + books['categories'] + books['price']

new_df = books[['tags', 'title', 'id']].copy()

new_df['tags'] = new_df['tags'].apply(lambda x: " ".join(x))

new_df['tags'] = new_df['tags'].apply(lambda x: x.lower())

# from vietnamese stopwords wiki 
vietnamese_stopwords = [
    "bị", "bởi", "cả", "các", "cái", "cần", "càng", "chỉ", "chiếc", "cho", "chứ", "chưa", "chuyện", "có", "có_thể", 
    "cứ", "của", "cùng", "cũng", "đã", "đang", "đây", "để", "đến_nỗi", "đều", "điều", "do", "đó", "được", "dưới", 
    "gì", "khi", "không", "là", "lại", "lên", "lúc", "mà", "mỗi", "một_cách", "này", "nên", "nếu", "ngay", "nhiều", 
    "như", "nhưng", "những", "nơi", "nữa", "phải", "qua", "ra", "rằng", "rất", "rồi", "sau", "sẽ", "so", "sự", "tại", 
    "theo", "thì", "trên", "trước", "từ", "từng", "và", "vẫn", "vào", "vậy", "vì", "việc", "với", "vừa"
]

cv = CountVectorizer(max_features=5000, stop_words=vietnamese_stopwords)

# get all the words and check each row with it
vector = cv.fit_transform(new_df['tags']).toarray()

# calculatethe similarity between vector
similarity = cosine_similarity(vector)

# save as pickle files
pickle.dump(new_df, open("pickle_files/books.pkl", 'wb'))
pickle.dump(similarity, open("pickle_files/similarity.pkl", 'wb'))

        









