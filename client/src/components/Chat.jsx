import {useNavigate,useParams } from "react-router-dom";
import { Row,Container,Col,Button, Spinner, Stack, Badge } from "react-bootstrap";
import React, { useEffect, useState } from "react";
import API from "../API";
import {useStatusAlert} from "../../hooks/useStatusAlert";
import { handleApiError, handleErrorResponse } from "../utils/utils";
import {
    MDBContainer,
    MDBRow,
    MDBCol,
    MDBCard,
    MDBCardBody,
    MDBIcon,
    MDBBtn,
    MDBTypography,
    MDBTextArea,
    MDBCardHeader,
  } from "mdb-react-ui-kit";



function Chat({userInfo}){
    const {ticketId} = useParams();
    const navigate = useNavigate();
    const [ticketData, setTicketData] = useState(null);
    const [loading, setLoading] = useState(true)
    const [load, setLoad] = useState(true)
    const [error,setError] = useState(false)
    const {StatusAlertComponent, showError, resetStatusAlert} = useStatusAlert();
    const messages = [
      {
          "messageId": 1,
          "sender": "CUSTOMER",
          "expertId": 3,
          "content": "Ciao avrei bisogno dell'aiuto per la cocaina",
          "attachments": [],
          "time": 1688849877664
      },
      {
          "messageId": 2,
          "sender": "EXPERT",
          "expertId": 3,
          "content": "Prego dimmi pure pezzo di merda",
          "attachments": [],
          "time": 1688850055490
      },
      {
          "messageId": 3,
          "sender": "CUSTOMER",
          "expertId": 3,
          "content": "Era tutta una burla sono drogato di Allegrismo",
          "attachments": [
              {
                  "attachmentId": 1,
                  "name": "150345301-5f6a6750-769a-4fe1-9286-23169c121789.jpg",
                  "type": "JPEG"
              }
          ],
          "time": 1688850159452
      }
  ];

    useEffect(() => {
        if(load){
            setLoading(true)
            API.getTicketById(ticketId)
            .then((t)=>{
                setTicketData(t)
                setLoading(false)
                resetStatusAlert()
                setLoad(false)
            })
            .catch(err=> stopAnimationAndShowError(err))
        }   
    },
    [load]
    );

    const stopAnimationAndShowError = (err) => {
        setLoading(false)
        setError(true)
        handleApiError(err,showError)
    }

    

    return (
        <>
            <MDBContainer fluid className="py-5">
                <Row className="mb-3">
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
                        <Row  xs="auto">
                            <Col><h3>TicketID: <span className="grayed">#{ticketId}</span></h3></Col>
                            <Col> <h3>{ticketData.title}</h3></Col>
                        </Row>
                        <Row>
                            <Col> <h5>{ticketData.description} <Badge bg="secondary">{ticketData.status}</Badge> </h5></Col>
                        </Row>
                        <hr className="my-4 border-gray"/>
                        <MessageList messages={messages}/>
                    </>
                    : null
                }
            </MDBContainer>
        </>
      )
}

function MessageList({messages}) {
    return (
        <>
        <MDBRow className="justify-content-center">
            <MDBCol md="6" lg="7" xl="8">
            <MDBTypography listUnStyled>
            {
              messages.map((m)=><Message message={m} key={m.messageId}/>)
            }
            </MDBTypography>
            </MDBCol>
        </MDBRow>
        </>
    )
}

function Message({message}){
    return (
        <>
         <li className="d-flex justify-content-between mb-4">
              <img
                src="https://mdbcdn.b-cdn.net/img/Photos/Avatars/avatar-6.webp"
                alt="avatar"
                className="rounded-circle d-flex align-self-start me-3 shadow-1-strong"
                width="60"
              />
              <MDBCard>
                <MDBCardHeader className="d-flex justify-content-between p-3">
                  <p className="fw-bold mb-0">{message.sender}</p>
                  <p className="text-muted small mb-0">
                    <MDBIcon far icon="clock" /> {message.time}
                  </p>
                </MDBCardHeader>
                <MDBCardBody>
                  <p className="mb-0">
                   {message.content}
                  </p>
                </MDBCardBody>
              </MDBCard>
            </li>
        </>
    )
}

function MessageForm(){
  return (
    <>
    
    </>
  );
}

export default Chat;

