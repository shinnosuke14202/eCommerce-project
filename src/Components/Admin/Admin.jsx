/* eslint-disable react-hooks/exhaustive-deps */
import { Link, useNavigate } from "react-router-dom";
import { mainUrl } from "../../helper";
import { useEffect, useState } from "react";
import axios from "axios";
import "./Admin.scss";

const Admin = () => {
    const [data, setData] = useState([]);
    const pageSize = 10;
    const [text, setText] = useState("");
    const navigate = useNavigate();

    const [pages, setPages] = useState();
    const [currentPage, setCurrentPage] = useState();

    useEffect(() => {
        if (text == "") {
            fetchData(currentPage, pageSize);
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
        fetchData(0, pageSize);
    }, []);

    const fetchData = async (pageNo, pageSize) => {
        try {
            const res = await axios.get(
                `${mainUrl}/products-page?pageNo=${pageNo}&pageSize=${pageSize}`
            );
            setData(res.data.products);
            setCurrentPage(pageNo);
            setPages(
                Array.from(
                    { length: res.data.totalPages },
                    (currentElement, index) => index + 1
                )
            );
            // setProducts(res.data.products);
            // console.log(res.data);
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

    // console.log(pages);

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
            <div className="pages">
                {pages &&
                    pages.map((page, index) => (
                        <div
                            key={index}
                            className={`page ${
                                page - 1 === currentPage ? "active" : ""
                            }`}
                            onClick={() => {
                                fetchData(page - 1, pageSize)
                            }}
                        >
                            <p>{page}</p>
                        </div>
                    ))}
            </div>
        </div>
    );
};

export default Admin;
