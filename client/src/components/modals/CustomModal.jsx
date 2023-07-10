import {useStatusAlert} from "../../hooks/useStatusAlert";
import {Modal, Row} from "react-bootstrap";
import React, {createContext, useContext, useMemo} from "react";
import {ModalType} from "../../enums/ModalType";
import {TicketStatus} from "../../enums/TicketStatus";
import {StatusChangeInProgressModalBody} from "./bodies/StatusChangeInProgressModalBody";
import {StatusChangeStandardModalBody} from "./bodies/StatusChangeStandardModalBody";
import {CreateTicketModalBody} from "./bodies/CreateTicketModalBody";
import {OperationCompletedModalBody} from "./bodies/OperationCompletedModalBody";
import {RegisterProductModalBody} from "./bodies/RegisterProductModalBody";
import {getModalSize, getModalTitle} from "../../utils/modalUtil";

const ModalContext = createContext(null);

function CustomModal({
                         show,
                         hide,
                         backdrop = true,
                         keyboard = true,
                         type,
                         desiredState = null,
                         objectId = null,
                         completingAction = null
                     }) {

    const {StatusAlertComponent, showError, resetStatusAlert} = useStatusAlert();

    const handleClose = () => {
        hide();
        resetStatusAlert()
    }

    const ModalProviderValue
        = useMemo(() =>
            ({type, desiredState, StatusAlertComponent, objectId, handleClose, completingAction, showError}),
        [type, desiredState, StatusAlertComponent, objectId, handleClose, completingAction, showError]);

    const modalSize = getModalSize(type)

    return (
        <Modal
            show={show}
            onHide={handleClose}
            backdrop={backdrop}
            size={modalSize}
            keyboard={keyboard}
            centered
        >
            <ModalContext.Provider value={ModalProviderValue}>
                <CustomModalHeader/>
                <CustomModalBody/>
            </ModalContext.Provider>
        </Modal>
    )
}

function CustomModalHeader() {
    const {type, desiredState, StatusAlertComponent} = useContext(ModalContext)
    const modalTitle = getModalTitle(type, desiredState)
    return (
        <>
            <Modal.Header closeButton>
                <Modal.Title>
                    {
                        modalTitle
                    }
                </Modal.Title>
            </Modal.Header>
            <Row className="mt-2 mx-3">
                <StatusAlertComponent/>
            </Row>
        </>
    )
}

function CustomModalBody() {
    const {type, desiredState} = useContext(ModalContext)
    switch (type) {
        case ModalType.STATUS_CHANGE:
            if (desiredState === TicketStatus.IN_PROGRESS)
                return <StatusChangeInProgressModalBody/>
            else
                return <StatusChangeStandardModalBody/>
        case ModalType.CREATE_TICKET:
            return <CreateTicketModalBody/>
        case ModalType.CONFIRM_STATUS_CHANGE:
            return <OperationCompletedModalBody description="Status change successfully concluded"/>
        case ModalType.CONFIRM_CREATE:
            return <OperationCompletedModalBody
                description="A support ticket for the selected product was successfully created"/>
        case ModalType.REGISTER_PRODUCT:
            return <RegisterProductModalBody/>
        case ModalType.CONFIRM_REGISTER:
            return <OperationCompletedModalBody
                description="The purchased product was correctly registered in the system"/>
    }
}

export {CustomModal, ModalContext as CustomModalContext};
