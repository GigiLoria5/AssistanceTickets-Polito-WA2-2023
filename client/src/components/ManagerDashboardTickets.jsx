import {useEffect, useState} from "react";
import {useStatusAlert} from "../hooks/useStatusAlert";
import API from "../API";
import {Col, Container, Row, Spinner, Dropdown, DropdownButton} from "react-bootstrap";
import {handleApiError} from "../utils/utils";
import Tickets from "./Tickets";
import ClientInfoCanvas from "./ClientInfoCanvas";
import ExpertInfoCanvas from "./ExpertInfoCanvas";
import useTicketNavigation from "../hooks/useTicketNavigation";

function ManagerDashboardTickets() {
    const [tickets, setTickets] = useState(null);
    const [products, setProducts] = useState(null);
    const [clientInfo, setClientInfo] = useState(null)
    const [expertInfo, setExpertInfo] = useState(null)
    const [loading, setLoading] = useState(true)
    const [load, setLoad] = useState(true)
    const {StatusAlertComponent, showError, resetStatusAlert} = useStatusAlert();
    const {actionGoToTicket, formatTickets, showClient, setShowClient} = useTicketNavigation(products, tickets);

    const stopAnimationAndShowError = (err) => {
        setLoading(false)
        handleApiError(err, showError)
    }

    useEffect(() => {
            const getData = async () => {
                await Promise.all([getTicketsData(), getProductsData()])
            }
            if (load === true) {
                setLoading(true)
                getData()
                    .then(() => {
                        setLoading(false)
                        resetStatusAlert()
                    }).catch(err => stopAnimationAndShowError(err))
                    .finally(() => {
                        setLoad(false)
                    })
            }
        }
        ,
        [load]
    )

    const getTicketsData = async () => {
        return new Promise((resolve, reject) => {
            API.getAllTickets()
                .then((t) => {
                        setTickets(t)
                        resolve()
                    }
                ).catch(err => reject(err))
        })
    }

    const getProductsData = async () => {
        return new Promise((resolve, reject) => {
            API.getAllProducts()
                .then((p) => {
                        setProducts(p)
                        resolve()
                    }
                ).catch(err => reject(err))
        })
    }

    const getClientInfo = (clientId) => {
        API.getProfileById(clientId)
            .then((client) => {
                setClientInfo(client)
                resetStatusAlert()
            })
            .catch(err => handleApiError(err, showError))
    }

    const getExpertInfo = (expertId) => {
        API.getExpertById(expertId)
            .then((expert) => {
                setExpertInfo(expert)
                resetStatusAlert()
            })
            .catch(err => handleApiError(err, showError))
    }
    const [showExpert, setShowExpert] = useState(false)

    const handleCloseClient = () => setShowClient(false)
    const handleCloseExpert = () => setShowExpert(false)

    const handleShowClientInfo = (clientId) => {
        getClientInfo(clientId);
        setShowClient(true)
    }

    const handleShowExpertInfo = (expertId) => {
        getExpertInfo(expertId);
        setShowExpert(true)
    }

    const [sorting, setSorting] = useState("Last Modified At")

    return (
        <Container className="h-100">
            <Row className="h-100">
                <Col className="d-flex flex-column align-items-center justify-content-center">
                    <h2>Tickets Management</h2><StatusAlertComponent/>
                    {
                        tickets && !loading ?
                            <>
                                <FilterDropdown sorting={sorting} setSorting={setSorting}/>
                                <Tickets tickets={formatTickets()}
                                         actionName={"Details"}
                                         action={actionGoToTicket}
                                         showClientInfo={handleShowClientInfo}
                                         showExpertInfo={handleShowExpertInfo}
                                         sorting={sorting}
                                />
                                {clientInfo ?
                                    <ClientInfoCanvas show={showClient} onHide={handleCloseClient}
                                                      clientInfo={clientInfo}/> : null}
                                {expertInfo ?
                                    <ExpertInfoCanvas show={showExpert} onHide={handleCloseExpert}
                                                      expertInfo={expertInfo}/> : null}
                            </>
                            : <Spinner animation="border" variant="primary"/>
                    }
                </Col>
            </Row>
        </Container>
    );
}

function FilterDropdown({sorting, setSorting}) {
    return (
        <>
            <h6>Sort By:</h6>
            <DropdownButton id="dropdown-basic-button" title={sorting}>
                <Dropdown.Item onClick={() => setSorting("Last Modified At")}>Last Modified At</Dropdown.Item>
                <Dropdown.Item onClick={() => setSorting("Created At")}>Created At</Dropdown.Item>
                <Dropdown.Item onClick={() => setSorting("Priority Level")}>Priority Level</Dropdown.Item>
            </DropdownButton>
        </>
    );
}

export default ManagerDashboardTickets;
