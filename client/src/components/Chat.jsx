import {useNavigate,useParams } from "react-router-dom";
import { Row,Container,Col,Button, Spinner } from "react-bootstrap";
import React, { useEffect, useState } from "react";
import API from "../API";
import {useStatusAlert} from "../../hooks/useStatusAlert";
import { handleApiError, handleErrorResponse } from "../utils/utils";



function Chat({userInfo}){
    const {ticketId} = useParams();
    const navigate = useNavigate();
    const [ticketData, setTicketData] = useState(null);
    const [loading, setLoading] = useState(true)
    const [load, setLoad] = useState(true)
    const [error,setError] = useState(false)
    const {StatusAlertComponent, showError, resetStatusAlert} = useStatusAlert();

    useEffect(() => {
        const getData = async () => {
            await Promise(getTicketData())
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
        setError(true)
        handleApiError(err,showError)
    }

    const getTicketData =  () => {
        API.getTicketById(ticketId)
            .then((t)=> {
                setTicketData(t)
                resetStatusAlert()
            })
            .catch(err => handleApiError(err,showError))
    }
    

    return (
        <>
            <Container>
                <Row>
                <Col md="auto" className="d-flex align-items-center">
                    <Button onClick={() => navigate(`/tickets/${ticketId}`)}> {"Go back"}</Button>
                </Col>
                <Col md="auto" className="d-flex align-items-center">
                    <h1>Chat</h1>
                </Col>
                </Row>
                <StatusAlertComponent/>
                {
                    loading ?
                    <Spinner animation="border" variant="primary"/>
                    : !error ?
                    <>
                        <h4>All ok</h4>
                    </>
                    : null
                }
            </Container>
        </>
      )
}

function MessageList() {
    return (
        <div>List</div>
    )
}

function Message(){
    return (
        <div>Message</div>
    )
}

export default Chat;

