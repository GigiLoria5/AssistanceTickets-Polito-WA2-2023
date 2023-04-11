import React, {useEffect, useState} from "react";
import {Alert, Row, Col, Form, Button, Table} from "react-bootstrap";
import API from "../API";

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
                    {data.map((item, index) => (
                        <tr key={index}>
                            <td>{item.id}</td>
                            <td>{item.asin}</td>
                            <td>{item.brand}</td>
                            <td>{item.category}</td>
                            <td>{item.manufacturerNumber}</td>
                            <td>{item.name}</td>
                            <td>{item.price}</td>
                            <td>{item.weight}</td>
                        </tr>
                    ))}
                    </tbody>
                </Table>
            </Col>
        </Row>
    );
};

const ErrorPopup = ({error}) => {
    return (
        <Row className="mt-3">
            <Col>
                <Alert variant="danger" className="roundedError">
                    <Alert.Heading>{error}</Alert.Heading>
                </Alert>
            </Col>
        </Row>
    )
}

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
                    <ErrorPopup error={error}/>
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

export default Products;
