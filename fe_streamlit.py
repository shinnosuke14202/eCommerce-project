import pickle
import streamlit as st

books = pickle.load(open("./pickle_files/books.pkl", "rb"))
similarity = pickle.load(open("./pickle_files/similarity.pkl", "rb"))

def recommend(selected_book):

    index = books[books['title'] == selected_book].index[0]

    distances = sorted(list(enumerate(similarity[index])),
                    reverse=True, key=lambda x: x[1])

    recommended_books = []

    for i in distances[1:6]:
        recommended_books.append(books.iloc[i[0]].title)

    return recommended_books

def main():

    st.header("Books Recommendation System")

    book_list = books['title'].values

    selected_book = st.selectbox(
        "Type or select a book to get recommendations",
        book_list
    )

    if st.button('Show recommendation'):

        recommended_books = recommend(selected_book)

        for book in recommended_books:
            st.write(book)


if __name__ == '__main__':
    main()