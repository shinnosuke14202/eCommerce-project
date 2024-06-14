/* eslint-disable react-hooks/exhaustive-deps */
import { Link, useNavigate } from "react-router-dom";
import { mainUrl } from "../../helper";
import { useEffect, useState } from "react";
import axios from "axios";
import "./Admin.scss";

const OldAdmin = () => {

    const [data, setData] = useState([]);
    const [text, setText] = useState("");
    const navigate = useNavigate();

    useEffect(() => {
        if (text == "") {
            fetchData();
        }
        else {
            const searchForBooks = async () => {
                const res = await axios.get(
                    `${mainUrl}/products/search/${text}`
                );
                setData(res.data)
            }
            searchForBooks();
        }
    }, [text]);

    useEffect(() => {
        fetchData();
    }, []);

    const fetchData = async () => {
        try {
            const res = await axios.get(
                `${mainUrl}/products`
            );
            setData(res.data);
        } catch (err) {
            console.log(err);
        }
    };

    const handleDelete = (id) => {
        if (window.confirm("Do you want to delete this book?")) {
            const deleteBook = async () => {
                try {
                    await axios.delete(`${mainUrl}/products/${id}`);
                    navigate(0);
                } catch (err) {
                    console.log(err);
                }
            };
            deleteBook();
        }
    };

    return (
        <div className="admin">
            <div className="topBox">
                <Link to="/edit/-1" className="link addLink">
                    <div className="addBookBtn">Thêm sách</div>
                </Link>
                <input
                    type="text"
                    className="searchBar"
                    placeholder="Search for book"
                    onChange={(e) => setText(e.target.value)}
                    value={text}
                />
            </div>
            <div className="title">ADMIN</div>
            <table className="adminTable">
                <thead>
                    <tr>
                        <td>ID</td>
                        <td>Tiêu đề</td>
                        <td>Tác giả</td>
                        <td>Bìa sách</td>
                        <td>Thể loại</td>
                        <td>Giá sách</td>
                        <td>Trạng thái</td>
                        {/* <td>Lượt bấm</td> */}
                        <td>View</td>
                        <td>Delete</td>
                    </tr>
                </thead>
                <tbody>
                    {data.map((item, index) => (
                        <tr key={item.id}>
                            <td>{index + 1}</td>
                            <td>{item.title}</td>
                            <td>{item.author}</td>
                            <td>
                                <img
                                    src={`data:image/png;base64,${item.image}`}
                                ></img>
                            </td>
                            <td>
                                {item.categories.map((category, index) => (
                                    <div key={index}>
                                        {category.categoryName}
                                    </div>
                                ))}
                            </td>
                            <td>{item.price}</td>
                            <td>{item.type}</td>
                            {/* <td>{item.clickAfterSuggestByAI}</td> */}
                            <td>
                                <Link to={`/edit/${item.id}`} className="link">
                                    <i className="fa-solid fa-file-pen"></i>
                                </Link>
                            </td>
                            <td>
                                <i
                                    className="fa-solid fa-trash deleteIcon"
                                    onClick={() => handleDelete(item.id)}
                                ></i>
                            </td>
                        </tr>
                    ))}
                </tbody>
            </table>
        </div>
    );
};

export default OldAdmin;
