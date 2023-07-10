import { useNavigate, useParams } from "react-router-dom";
import { Row, Container, Col, Button, Spinner, Stack, Badge } from "react-bootstrap";
import React, { useEffect, useState } from "react";
import API from "../API";
import { useStatusAlert } from "../../hooks/useStatusAlert";
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
import dayjs from "dayjs";
import relativeTime from 'dayjs/plugin/relativeTime';
dayjs.extend(relativeTime);
import { saveAs } from 'file-saver';


function Chat({ userInfo }) {
  const { ticketId } = useParams();
  const navigate = useNavigate();
  const [ticketData, setTicketData] = useState(null);
  const [loading, setLoading] = useState(true)
  const [load, setLoad] = useState(true)
  const [error, setError] = useState(false)
  const { StatusAlertComponent, showError, resetStatusAlert } = useStatusAlert();
  const [messages, setMessages] = useState(null);
  const [customer, setCustomer] = useState(null);


  useEffect(() => {
    const getData = async () => {
      await Promise.all([
        getTicketById(),
        getAllMessagesByTicketId()
      ]);
    };

    if (load === true) {
      setLoading(true);
      getData()
        .then(() => {
          setLoading(false);
          resetStatusAlert();
        })
        .catch(err => stopAnimationAndShowError(err))
        .finally(() => {
          setLoad(false);
        });
    }
  }, [load]);

  const stopAnimationAndShowError = (err) => {
    setLoading(false)
    setError(true)
    handleApiError(err, showError)
  }

  const getTicketById = async () => {
    return new Promise((resolve, reject) => {

      API.getTicketById(ticketId)
        .then((t) => {
          setTicketData(t)
          if (userInfo.role === "Expert") {
            resolve(getProfileById(t.customerId))
          } else {
            resolve()
          }
        })
        .catch(e => reject(e))
    })
  }

  const getProfileById = async (customerId) => {
    return new Promise((resolve, reject) => {

      API.getProfileById(customerId)
        .then((t) => {
          setCustomer(t)
          resolve()
        })
        .catch(e => reject(e))
    })
  }


  const getAllMessagesByTicketId = async () => {

    return new Promise((resolve, reject) => {

      API.getAllMessagesByTicketId(ticketId)
        .then((m) => {
          setMessages(m)
          // fare richiesta profileID
          resolve()
        })
        .catch(e => reject(e))
    })
  }

  async function getAttachment(attachmentId, messageId, filename) {
    console.log(attachmentId, messageId)
    try {
      const blob = await API.getAttachment(ticketId, messageId, attachmentId)
      const url = URL.createObjectURL(blob);
      console.log(url)
      saveAs(url, filename)
    } catch (error) {
      handleApiError(error, showError)
    }
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
        <StatusAlertComponent />
        {
          loading ?
            <Spinner animation="border" variant="primary" />
            : !error ?
              <>
                <Row xs="auto">
                  <Col><h3>TicketID: <span className="grayed">#{ticketId}</span></h3></Col>
                  <Col><h3>{ticketData.title}</h3></Col>
                </Row>
                <Row>
                  <Col><h5>{ticketData.description} <Badge bg="secondary">{ticketData.status}</Badge></h5>
                  </Col>
                </Row>
                <hr className="my-4 border-gray" />
                <MessageList messages={messages} userInfo={userInfo} customer={customer} ticketId={ticketId} getAttachment={getAttachment} />
              </>
              : null
        }
      </MDBContainer>
    </>
  )
}

function MessageList({ messages, userInfo, customer, ticketId, getAttachment }) {
  return (
    <>
      <MDBRow className="justify-content-center">
        <MDBCol md="6" lg="7" xl="8">
          <MDBTypography listUnStyled>
            {
              messages.map((m) => <Message message={m} key={m.messageId} customerInfo={customer} userInfo={userInfo} ticketId={ticketId} getAttachment={getAttachment} />)
            }
          </MDBTypography>
        </MDBCol>
      </MDBRow>
    </>
  )
}

// useEffect(() => {
//   const fetchData = async () => {
//     const data = await Promise.all(
//       message.attachments.map(attach =>
//         API.getAttachment(ticketId, message.messageId, attach.attachmentId))
//     );
//     setAttachmentData(data);
//   }
//   fetchData();
// }, [message.attachments, message.messageId, ticketId])

function Message({ message, customerInfo, userInfo, getAttachment }) {
  return (
    <>

      <li className="d-flex justify-content-between mb-4">
        <img
          src="https://mdbcdn.b-cdn.net/img/Photos/Avatars/avatar-6.webp"
          alt="avatar"
          className="rounded-circle d-flex align-self-start me-3 shadow-1-strong"
          width="60"
        />
        <MDBCard className="w-100">
          <MDBCardHeader className="d-flex justify-content-between p-3">
            <div className="d-flex justify-content-start">
              <p className="fw-bold mb-0 me-2">{userInfo.role === "Expert" ? (message.sender === "EXPERT" ? userInfo.name : customerInfo.name) : (message.sender === "CUSTOMER" ? userInfo.name : "Expert")}</p>
              <p className="text-muted small mb-0"> {message.sender}</p>
            </div>
            <p className="text-muted small mb-0">
              <MDBIcon far icon="clock" /> {dayjs(message.time).fromNow()}
            </p>
          </MDBCardHeader>
          <MDBCardBody>
            <p className="mb-3">
              {message.content}
            </p>
            {message.attachments && message.attachments.length > 0 ? <AttachmentList attachments={message.attachments} getAttachment={getAttachment} messageId={message.messageId} /> : null}

          </MDBCardBody>
        </MDBCard>
      </li>
    </>
  )
}

function AttachmentList({ attachments, getAttachment, messageId }) {
  return (
    <ul>
      {attachments.map((a) => (
        <li key={a.attachmentId}>
          <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" className="bi bi-paperclip" viewBox="0 0 16 16">
            <path d="M4.5 3a2.5 2.5 0 0 1 5 0v9a1.5 1.5 0 0 1-3 0V5a.5.5 0 0 1 1 0v7a.5.5 0 0 0 1 0V3a1.5 1.5 0 1 0-3 0v9a2.5 2.5 0 0 0 5 0V5a.5.5 0 0 1 1 0v7a3.5 3.5 0 1 1-7 0V3z" />
          </svg>
          <a href="#" onClick={() => getAttachment(a.attachmentId, messageId, a.name)}>
            {a.name}
          </a>
        </li>))}
    </ul>
  );
}


function MessageForm() {
  return (
    <>

    </>
  );
}

export default Chat;

