import React, {useEffect, useState} from "react";
import {Button, Col, Form, Row, Table} from "react-bootstrap";
import API from "../API";
import StatusAlert from "./StatusAlert";

const Products = () => {
    const [data, setData] = useState(null);
    const [error, setError] = useState("")

    const getAllProducts = () => {
        API.getAllProducts().then((x) => {
                setData(x)
                setError("")
            }
        ).catch(err => {
            setError(err.error);
        })
    }

    const searchProduct = (productId) => {
        API.searchProduct(productId).then((x) => {
                setData([x])
                setError("")
            }
        ).catch(err => {
            setError(err.error);
        })
    }

    useEffect(() => {
            getAllProducts()
        }, []
    );

    return (
        <>
            {
                error ?
                    <StatusAlert message={error}/>
                    : null
            }
            {
                data ?
                    <>
                        <ProdSearchBar getAllProducts={getAllProducts} searchProduct={searchProduct}/>
                        <ProdTable data={data}/>
                    </>
                    : null
            }
        </>
    );
};

const ProdSearchBar = ({getAllProducts, searchProduct}) => {
    const [searchValue, setSearchValue] = useState("");

    const handleSearch = () => {
        const trimmedValue = searchValue.trim();
        if (trimmedValue) {
            searchProduct(trimmedValue);
        }
    };

    const handleReset = () => {
        setSearchValue("");
        getAllProducts()
    };

    const handleKeyPress = (event) => {
        if (event.key === "Enter") {
            event.preventDefault();
            handleSearch();
        }
    };

    return (
        <Row className="mt-3">
            <Col>
                <Form>
                    <Form.Group>
                        <Form.Control
                            type="search"
                            placeholder="Search by id"
                            value={searchValue}
                            onChange={(event) => setSearchValue(event.target.value)}
                            onKeyDown={handleKeyPress}
                        />
                    </Form.Group>
                </Form>
            </Col>
            <Col xs="auto">
                <Button variant="primary" className="mx-2" onClick={handleSearch}>
                    Search
                </Button>
                <Button variant="secondary" onClick={handleReset}>
                    Reset
                </Button>
            </Col>
        </Row>
    );
};

const ProdTable = ({data}) => {
    return (
        <Row className="mt-3">
            <Col>
                <Table striped bordered hover>
                    <thead>
                    <tr>
                        <th>productId</th>
                        <th>asin</th>
                        <th>brand</th>
                        <th>category</th>
                        <th>manufacturerNumber</th>
                        <th>name</th>
                        <th>price</th>
                        <th>weight</th>
                    </tr>
                    </thead>
                    <tbody>
                    {data.map((product) => (
                        <tr key={product.productId}>
                            <td>{product.productId}</td>
                            <td>{product.asin}</td>
                            <td>{product.brand}</td>
                            <td>{product.category}</td>
                            <td>{product.manufacturerNumber}</td>
                            <td>{product.name}</td>
                            <td>{product.price}</td>
                            <td>{product.weight}</td>
                        </tr>
                    ))}
                    </tbody>
                </Table>
            </Col>
        </Row>
    );
};

export default Products;
