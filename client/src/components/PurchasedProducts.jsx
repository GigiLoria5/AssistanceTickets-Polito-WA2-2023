import {Button, Col, Row, Table} from "react-bootstrap";
import React from "react";
import {dateTimeMillisFormatted} from "../utils/utils";

function PurchasedProducts({purchases, title, actionNameFinder, action}) {

    return (
        <>
            <h4>
                {title}
            </h4>
            <Row>
                <Col>
                    <PurchasedProductsTable purchases={purchases} actionNameFinder={actionNameFinder}
                                            action={action}/>
                </Col>
            </Row>
        </>

    )
}

function PurchasedProductsTable({purchases, actionNameFinder, action}) {
    return (
        (purchases.length) > 0 ?
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
                    {purchases.map((purchase) => (
                        <tr key={purchase.product.productId}>
                            <td>{purchase.product.name}</td>
                            <td>{purchase.product.asin}</td>
                            <td>{purchase.product.brand}</td>
                            <td>{dateTimeMillisFormatted(purchase.createdAt)}</td>
                            <td>{dateTimeMillisFormatted(purchase.registeredAt)}</td>
                            <td>
                                <Row>
                                    <Col className="d-grid gap-2">
                                        <Button
                                            variant={purchase.ticketId ? "outline-primary" : "primary"}
                                            onClick={() => action(purchase)}
                                        >
                                            {actionNameFinder(purchase)}
                                        </Button>
                                    </Col>
                                </Row>
                            </td>
                        </tr>
                    ))}
                    </tbody>
                </Table>
            </div> : <div>No purchases found</div>
    );
}


export default PurchasedProducts;
