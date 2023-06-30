import React, {useEffect, useState} from "react";
import {useStatusAlert} from "../../hooks/useStatusAlert";
import API from "../API";
import {Button, Col, Row, Spinner, Tab, Tabs} from "react-bootstrap";
import {CustomModal} from "./Modals";
import {ModalType} from "../../enums/ModalType";


function ClientDashboard({userInfo}) {
    const [ticketsData, setTicketsData] = useState(null);
    const [productsData, setProductsData] = useState(null);
    const [loading, setLoading] = useState(true)
    const [load, setLoad] = useState(true)
    const [errorPresence, setErrorPresence] = useState(false)
    const {StatusAlertComponent, showError, resetStatusAlert} = useStatusAlert();
    const [confirmModalShow, setConfirmModalShow] = React.useState(false);

    useEffect(() => {
            const getData = async () => {
                await Promise.all([getTicketsData(), getProductsData()])
            }
            if (load === true) {
                setLoading(true)
                getData().then(() => {
                    setLoading(false)
                    resetStatusAlert()
                }).catch(err => stopAnimationAndShowError(err)).finally(() => {
                    setLoad(false)
                })
            }
        }
        ,
        [load]
    )

    const stopAnimationAndShowError = (err) => {
        setLoading(false)
        setErrorPresence(true)
        showError(err.error)
    }

    const getTicketsData = async () => {
        console.log(userInfo.email)
        return new Promise((resolve, reject) => {
            API.getTicketsOfProfileByEmail(userInfo.email)
                .then((t) => {
                        setTicketsData(t)
                        resolve()
                    }
                ).catch(e => reject(e))
        })
    }

    const getProductsData = async () => {
        return new Promise((resolve, reject) => {
            API.getAllProducts()
                .then((p) => {
                        setProductsData(p)
                        resolve()
                    }
                ).catch(e => reject(e))
        })
    }

    return (
        <>
            <div className='mb-5'>
                <h1>Client dashboard</h1>
            </div>
            <StatusAlertComponent/>
            {
                loading ?
                    <Spinner animation="border" variant="primary"/>
                    :
                    !errorPresence ?
                        <>
                            <Row className='mb-4'>
                                <Col>
                                    <RegisterProduct update={() => {
                                        setLoad(true);
                                        setConfirmModalShow(true)
                                    }}/>
                                </Col>
                            </Row>
                            <Row className='mb-4'>
                                <Col>
                                    <ClientDashboardTabs ticketsData={ticketsData}
                                                         productsData={productsData}
                                                         update={() => {
                                                             setLoad(true);
                                                             setConfirmModalShow(true)
                                                         }}
                                    />
                                </Col>
                            </Row>

                            <CustomModal
                                show={confirmModalShow}
                                onHide={() => setConfirmModalShow(false)}
                                title="Titolo"
                                description="Descrizione"
                            />
                        </>
                        :
                        null
            }
        </>
    )
}

function RegisterProduct({update}) {
    const [registerProductModalShow, setRegisterProductModalShow] = React.useState(false);

    return (
        <>
            <Button onClick={() => setRegisterProductModalShow(true)}>
                Register purchase
            </Button>
            <CustomModal
                show={registerProductModalShow}
                completingAction={update}
                hide={() => setRegisterProductModalShow(false)}
                type={ModalType.REGISTER_PRODUCT}
            />
        </>
    )
}

function ClientDashboardTabs({ticketsData, productsData, update}) {
    return (
        <Tabs
            defaultActiveKey="products"
            id="uncontrolled-tab-example"
            className="mb-3"
        >
            <Tab eventKey="products" title="Purchased products">
                <ProductsTable products={productsData}/>
            </Tab>
            <Tab eventKey="tickets" title="My tickets">
                <TicketsTable tickets={ticketsData}/>
            </Tab>

        </Tabs>
    )
}

function TicketsTable({tickets}) {
    return (
        <>Tickets table</>
            )
            }

            function ProductsTable({products}){
            return (
            <>Products table</>
            )
        }

            export default ClientDashboard;
