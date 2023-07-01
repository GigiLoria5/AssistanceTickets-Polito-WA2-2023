import {Button, Col, Row, Table} from "react-bootstrap";
import React from "react";
import {dateTimeMillisFormatted} from "../utils/utils";

function PurchasedProducts({products, title, actionNameFinder, action}) {

    return (
        <>
            <h4>
                {title}
            </h4>
            <Row>
                <Col>
                    <PurchasedProductsTable products={products} actionNameFinder={actionNameFinder}
                                            action={action}/>
                </Col>
            </Row>
        </>

    )
}

function PurchasedProductsTable({products, actionNameFinder, action}) {
    return (
        (products.length) > 0 ?
            <div className="table-responsive">
                <Table>
                    <thead>
                    <tr>
                        <th>Name</th>
                        <th>Asin</th>
                        <th>Brand</th>
                        <th>Purchase date</th>
                        <th>Registration date</th>
                        <th></th>
                    </tr>
                    </thead>
                    <tbody>
                    {products.map((product) => (
                        <tr key={product.productId}>
                            <td>{product.name}</td>
                            <td>{product.asin}</td>
                            <td>{product.brand}</td>
                            <td>{dateTimeMillisFormatted(product.purchaseDate)}</td>
                            <td>{dateTimeMillisFormatted(product.registrationDate)}</td>
                            <td>
                                <Row >
                                    <Col className="d-grid gap-2">
                                        <Button
                                            variant={product.ticketId ? "outline-primary" : "primary"}
                                            onClick={() => action(product)}
                                        >
                                            {actionNameFinder(product)}
                                        </Button>
                                    </Col>
                                </Row>
                            </td>
                        </tr>
                    ))}
                    </tbody>
                </Table>
            </div> : <div>No tickets found</div>

    );

}


export default PurchasedProducts;
