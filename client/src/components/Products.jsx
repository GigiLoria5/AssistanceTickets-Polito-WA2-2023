import React, {useEffect, useState} from "react";
import {Button, Col, Form, Modal, Row, Table} from "react-bootstrap";
import API from "../API";
import {useStatusAlert} from "../../hooks/useStatusAlert";
import {UserRole} from "../../enums/UserRole";
import {handleApiError} from "../utils/utils";

const Products = ({userRole}) => {
    const [data, setData] = useState(null);
    const {StatusAlertComponent, showError, resetStatusAlert} = useStatusAlert();

    useEffect(() => {
            getAllProducts()
        }, []
    );

    const getAllProducts = () => {
        API.getAllProducts()
            .then((x) => {
                    setData(x)
                    resetStatusAlert()
                }
            )
            .catch(err => handleApiError(err, showError))
    }

    const searchProduct = (productId) => {
        API.searchProduct(productId)
            .then((x) => {
                    setData([x])
                    resetStatusAlert()
                }
            )
            .catch(err => handleApiError(err, showError))
    }

    return (
        <>
            <StatusAlertComponent/>
            {
                data ?
                    <>
                        <ProdSearchBar getAllProducts={getAllProducts} searchProduct={searchProduct}/>
                        <ProdTable data={data} allowGeneration={userRole && userRole === UserRole.MANAGER}/>
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

const ProdTable = ({data, allowGeneration}) => {
    const [show, setShow] = useState(false);
    const [token, setToken] = useState("");
    const {StatusAlertComponent, showError, resetStatusAlert} = useStatusAlert();

    const handleShow = () => setShow(true);

    const handleClose = () => {
        setShow(false);
        setToken("");
    }

    const handleGenerateToken = (productId) => {
        handleShow()
        API.generateToken(productId)
            .then(token => {
                resetStatusAlert();
                setTimeout(() => {
                    setToken(token);
                    navigator.clipboard.writeText(`${token}`).then(_ => {
                    })
                }, 500);
            })
            .catch(err => {
                handleApiError(err, showError);
                handleClose();
            })
    };

    return (
        <>
            <Modal
                show={show}
                onHide={handleClose}
                backdrop="static"
                keyboard={false}
                centered
            >
                <Modal.Header closeButton>
                    <Modal.Title>{token ? "Token Generated" : "Generating Token..."}</Modal.Title>
                </Modal.Header>
                <Modal.Body>
                    {token ? `${token}` : "Wait for the token to be created"}
                </Modal.Body>
                {token && <Modal.Footer>
                    <p className="text-muted">Token copied to clipboard</p>
                    <Button variant="primary" onClick={handleClose}>Close</Button>
                </Modal.Footer>}
            </Modal>
            <Row className="mt-3">
                <StatusAlertComponent/>
            </Row>
            <Row className="mt-3">
                <Col>
                    <Table striped bordered hover>
                        <thead>
                        <tr>
                            <th>ID</th>
                            <th>Asin</th>
                            <th>Brand</th>
                            <th>Category</th>
                            <th>Manufacturer Number</th>
                            <th>Name</th>
                            <th>Price</th>
                            <th>Weight</th>
                            {allowGeneration && <th>Token</th>}
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
                                {allowGeneration &&
                                    <td><Button
                                        onClick={() => handleGenerateToken((product.productId))}>Generate</Button></td>}
                            </tr>
                        ))}
                        </tbody>
                    </Table>
                </Col>
            </Row>
        </>
    );
};

export default Products;
