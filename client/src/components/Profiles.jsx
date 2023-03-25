import {Row, Col, Spinner, Button, Container,} from "react-bootstrap";
import SearchProfile from "./SearchProfile";
import DisplayProfile from "./DisplayProfile";

function Profiles(props) {

    return (
        <Container style={{ maxWidth: "75%" }}>
        <Row >
            <Col>
                <SearchProfile getProfile={props.getProfile}></SearchProfile>
            </Col>
            <Col>
                {'email' in props.profile && <DisplayProfile profile={props.profile}/>}
            </Col>
        </Row>

        <Row className="mt-3 mx-auto" style={{maxWidth:"25%"}}>
            <Button>Add new profile</Button>
        </Row>
        </Container>
    )
}

export default Profiles
