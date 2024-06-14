from flask import Flask, jsonify, request
from models import Book
import pickle

app = Flask(__name__)

# suggest
books = pickle.load(open("./pickle_files/books.pkl", "rb"))
similarity = pickle.load(open("./pickle_files/similarity.pkl", "rb"))

# suggest for holidays
book_data = pickle.load(open("./pickle_files/book_data.pkl", "rb"))
similarity_holidays = pickle.load(open("./pickle_files/similarity_holidays.pkl", "rb"))
holidays = pickle.load(open("./pickle_files/holidays.pkl", "rb"))

def recommend(selected_books):

    recommended_books = []

    for selected_book in selected_books:

        # print(selected_book)

        if not books[books['title'] == selected_book.strip()].empty:

            index = books[books['title'] == selected_book.strip()].index[0]

            distances = sorted(list(enumerate(similarity[index])),
                            reverse=True, key=lambda x: x[1])[1:6]

            for i in distances:
                book = Book(int(books.iloc[i[0]].id), books.iloc[i[0]].title)
                recommended_books.append(book.to_dict())
    
    return recommended_books

def recommend_books_base_on_holiday(date, lunar):

    books_holiday = []
    special_day = []
    holidays_size = holidays['description'].size

    if not holidays[holidays['date'] == date].empty:
        special_day.append(date)

    if not holidays[(holidays['date'] == lunar) & (holidays['lunar'] == 1)].empty:
        special_day.append(lunar)
    
    for day in special_day:

        holiday_index = holidays[holidays['date'] == day].index[0]

        distances = sorted(list(enumerate(similarity_holidays[holiday_index]))[holidays_size:], key=lambda x: x[1], reverse=True)[0:4]

        books_holiday.append({
            "id" : 0,
            "title" : holidays.loc[holiday_index].title
        })

        for distance in distances:
            index = distance[0] - holidays_size
            book = Book(int(book_data.iloc[index].id), book_data.iloc[index].title)
            books_holiday.append(book.to_dict())

    return books_holiday

@app.route('/recommendation', methods=['GET'])
def get_recommendations():

    requestBooks = request.args.getlist('bookTitles')
    requestBooks = requestBooks[0].split("+")

    if not requestBooks:
        return jsonify({'error': 'No books provided'}), 400
    recommendations = recommend(requestBooks)

    return jsonify(recommendations)


@app.route('/recommendation/holiday/<date>/<lunar>', methods=['GET'])
def get_recommendations_for_holiday(date, lunar):

    recommendations = recommend_books_base_on_holiday(date, lunar)

    return jsonify(recommendations)

if __name__ == '__main__':
    app.run(debug=True, host='0.0.0.0')
