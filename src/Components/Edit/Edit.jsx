/* eslint-disable react-hooks/exhaustive-deps */
import axios from "axios";
import { FormikProvider, useFormik } from "formik";
import { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import { toast } from "react-toastify";
import "react-toastify/dist/ReactToastify.css";
import * as yup from "yup";
import { mainUrl } from "../../helper";
import "./Edit.scss";

const Edit = () => {
    const id = useParams().id;

    const navigate = useNavigate();
    const [view, setView] = useState(id == "-1" ? false : true);
    const [data, setData] = useState("");
    const [cate, setCate] = useState([]);
    const [file, setFile] = useState();
    const [imgUpload, setImgUpload] = useState();
    const [cateIds, setCateIds] = useState(null);
    const [base64img, setImage] = useState();

    useEffect(() => {
        const fetchData = async () => {
            try {
                const res = await axios.get(`${mainUrl}/categories`);
                setCate(res.data);
            } catch (err) {
                console.log(err);
            }
        };
        fetchData();
    }, []);

    useEffect(() => {
        if (id > 0) {
            const fetchData = async () => {
                try {
                    const res = await axios.get(`${mainUrl}/products/${id}`);
                    setData(res.data);
                    setImgUpload(`data:image/png;base64, ${res.data.image}`);
                    setImage(res.data.image);
                } catch (err) {
                    console.log(err);
                }
            };
            fetchData();
        }
    }, []);

    useEffect(() => {
        if (data != "") {
            var id = [];
            var ids = [];
            data.categories.map((cate) => {
                id.push({ id: cate.id.toString() });
                ids.push(cate.id);
            });
            formik.setValues({
                title: data.title,
                author: data.author,
                description: data.description,
                type: data.type,
                clickAfterSuggestByAI: data.clickAfterSuggestByAI,
                category: id,
                price: data.price,
            });
            setCateIds(ids);
        }
    }, [data]);

    const uploadImg = (e) => {
        setFile(e.target.files[0]);
        setImgUpload(URL.createObjectURL(e.target.files[0]));
    };

    const formik = useFormik({
        initialValues: {
            title: "",
            author: "",
            description: "",
            clickAfterSuggestByAI: "",
            type: "",
            category: "",
            price: "",
        },

        validationSchema: yup.object({
            title: yup.string().required("Title can not be empty!"),
            author: yup.string().required("Author can not be empty!"),
            category: yup.array().required("Please select category!"),
        }),

        onSubmit: async (values) => {
            const formData = new FormData();

            const data = {
                id: id,
                title: values.title,
                author: values.author,
                description: values.description,
                type: values.type,
                clickAfterSuggestByAI: values.clickAfterSuggestByAI,
                categories: values.category,
                price: values.price,
            };

            //console.log(data.categories);

            formData.append("product", JSON.stringify(data));

            if (file != null) {
                formData.append("image", file);
            } else {
                
                // decodes a string of data which has been encoded using Base64 encoding
                var byteCharacters = atob(base64img); 

                // converts each character of the binary string into a numeric value representing a byte
                var byteNumbers = new Array(byteCharacters.length);
                for (var i = 0; i < byteCharacters.length; i++) {
                    byteNumbers[i] = byteCharacters.charCodeAt(i);
                }

                // handling binary data
                var byteArray = new Uint8Array(byteNumbers);
                
                var blob = new Blob([byteArray], { type: "image/jpeg" });
  
                // Create FormData object and append the file
                formData.append("image", blob, "image.jpg");
            }

            try {
                if (id < 0) {
                    if (window.confirm("Are you want to add this book?")) {
                        await axios.post(`${mainUrl}/products`, formData);
                        toast.success("Added Successfully!", {
                            hideProgressBar: true,
                            autoClose: 2000,
                        });
                        navigate("/admin");
                        // navigate("/old-admin")
                    } else {
                        console.log("You canceled!");
                    }
                } else {
                    await axios.put(`${mainUrl}/products/${id}`, formData);
                    toast.success("Updated Successfully!", {
                        hideProgressBar: true,
                        autoClose: 2000,
                    });
                }
            } catch (err) {
                console.log(err);
            }
        },
    });

    return (
        <FormikProvider value={formik}>
            <div className="edit">
                {id != "-1" ? (
                    <div className="top">
                        <h1>{data.title}</h1>
                    </div>
                ) : (
                    ""
                )}
                <form onSubmit={formik.handleSubmit} className="updateForm">
                    <div className="center">
                        <div className="leftSide">
                            <div className="r1">
                                <div className="box">
                                    <h2>Tiêu đề</h2>
                                    <input
                                        disabled={view}
                                        className="title"
                                        type="text"
                                        name="title"
                                        value={formik.values.title}
                                        onChange={formik.handleChange}
                                        onBlur={formik.handleBlur}
                                    />
                                    {formik.touched.title &&
                                    formik.errors.title ? (
                                        <p
                                            style={{
                                                color: "red",
                                                fontSize: "1.2rem",
                                            }}
                                        >
                                            {formik.errors.title}
                                        </p>
                                    ) : null}
                                </div>
                                <div className="box">
                                    <h2>Tác giả</h2>
                                    <input
                                        disabled={view}
                                        className="author"
                                        type="text"
                                        name="author"
                                        value={formik.values.author}
                                        onChange={formik.handleChange}
                                        onBlur={formik.handleBlur}
                                    />
                                    {formik.touched.author &&
                                    formik.errors.author ? (
                                        <p
                                            style={{
                                                color: "red",
                                                fontSize: "1.2rem",
                                            }}
                                        >
                                            {formik.errors.author}
                                        </p>
                                    ) : null}
                                </div>
                            </div>
                            <div className="r2">
                                <div className="box">
                                    <h2>Mô tả về sách</h2>
                                    <textarea
                                        disabled={view}
                                        className="description"
                                        type="text"
                                        name="description"
                                        value={formik.values.description}
                                        onChange={formik.handleChange}
                                    />
                                </div>
                            </div>
                            <div className="r3">
                                <div className="box">
                                    <h2>Trạng thái</h2>
                                    <input
                                        disabled={view}
                                        className="type"
                                        type="text"
                                        name="type"
                                        value={formik.values.type}
                                        onChange={formik.handleChange}
                                        onBlur={formik.handleBlur}
                                    />
                                </div>
                                {/* <div className="box">
                                    <h2>Lượt bấm</h2>
                                    <input
                                        disabled={view}
                                        className="clickAfterSuggestByAI"
                                        type="text"
                                        name="clickAfterSuggestByAI"
                                        value={
                                            formik.values.clickAfterSuggestByAI
                                        }
                                        onChange={formik.handleChange}
                                    />
                                </div> */}
                                <div className="box">
                                    <h2>Giá sách</h2>
                                    <input
                                        disabled={view}
                                        className="price"
                                        type="number"
                                        name="price"
                                        value={formik.values.price}
                                        onChange={formik.handleChange}
                                    />
                                </div>
                            </div>
                            <div className="r4">
                                {/* <div className="box">
                                    <h2>Giá sách</h2>
                                    <input
                                        disabled={view}
                                        className="price"
                                        type="number"
                                        name="price"
                                        value={formik.values.price}
                                        onChange={formik.handleChange}
                                    />
                                </div> */}
                            </div>
                            <div className="checkbox">
                                <h2>Thể loại</h2>
                                <div className="categoriesBox">
                                    {(!view || cateIds) &&
                                        cate.map((item) => (
                                            <div key={item.id}>
                                                <input
                                                    type="checkbox"
                                                    id={item.id}
                                                    name={item.categoryName}
                                                    value={item.id}
                                                    defaultChecked={view && cateIds.includes(
                                                        item.id
                                                    )}
                                                    onChange={(e) => {
                                                        const checkedCategoryId =
                                                            e.target.value;
                                                        const isChecked =
                                                            e.target.checked;
                                                        formik.setFieldValue(
                                                            "category",
                                                            isChecked
                                                                ? [
                                                                      ...formik
                                                                          .values
                                                                          .category,
                                                                      {
                                                                          id: checkedCategoryId,
                                                                      },
                                                                  ]
                                                                : formik.values.category.filter(
                                                                      (
                                                                          category
                                                                      ) =>
                                                                          category.id !==
                                                                          checkedCategoryId
                                                                  )
                                                        );
                                                        formik.handleChange(e);
                                                        console.log(formik.values.category);
                                                    }}
                                                    disabled={view}
                                                />
                                                <label htmlFor={item.id}>
                                                    {item.categoryName}
                                                </label>
                                            </div>
                                        ))}
                                </div>
                                {formik.touched.category &&
                                formik.errors.category ? (
                                    <p
                                        style={{
                                            color: "red",
                                            fontSize: "1.2rem",
                                        }}
                                    >
                                        {formik.errors.category}
                                    </p>
                                ) : null}
                            </div>
                        </div>
                        <div className="rightSide">
                            <label htmlFor="imgUpload" className="uploadBtn">
                                Upload
                            </label>
                            <input
                                type="file"
                                disabled={view}
                                name="imgUpload"
                                id="imgUpload"
                                className="imgUpload"
                                onChange={uploadImg}
                            />
                            <img src={imgUpload} alt="" />
                            {id == "-1" ? (
                                <button className="submitBtn" type="submit">
                                    ADD
                                </button>
                            ) : view == true ? (
                                <div
                                    className="submitBtn"
                                    onClick={() => setView(false)}
                                >
                                    EDIT
                                </div>
                            ) : (
                                <button className="submitBtn" type="submit">
                                    SAVE
                                </button>
                            )}
                        </div>
                    </div>
                </form>
            </div>
        </FormikProvider>
    );
};

export default Edit;
