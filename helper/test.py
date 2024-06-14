s = "a B BBB          D"
# print(s.split())

# from sklearn.metrics.pairwise import cosine_similarity

# a = [[1, 1, 1], [1, 0, 2]]

# cosine = cosine_similarity(a)
# print(cosine)

import pickle

book_data = pickle.load(open("../pickle_files/book_data.pkl", "rb"))
similarity_holidays = pickle.load(open("../pickle_files/similarity_holidays.pkl", "rb"))
holidays = pickle.load(open("../pickle_files/holidays.pkl", "rb"))

print(holidays)