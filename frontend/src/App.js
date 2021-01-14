import React, { useEffect, useState } from "react";
import Navbar from "./components/Navbar";
import { BrowserRouter as Router, Switch, Route } from "react-router-dom";
import { Container } from "react-bootstrap";
import { Jokes, Signup, Login, Home, Users, Logout } from "./components";
import { getUserByJwt } from "./utils/token";
import { loginMethod, logoutMethode } from "./utils/loginUtils";

function App() {
  const init = { username: "", roles: [] };
  const [user, setUser] = useState({ ...init });
  const [error, setError] = useState("");
  const login = (user, pass) => loginMethod(user, pass, setUser, setError);
  const logout = () => logoutMethode(setUser, init);

  useEffect(() => {
    if (getUserByJwt()) {
      setUser(getUserByJwt());
    }
  }, []);

  return (
    <Router>
      <Navbar user={user} logout={logout} />
      <Switch>
        <Route path="/" exact>
          <Home />
        </Route>
        <Route path="/jokes">
          <Jokes setError={setError}/>
        </Route>
        <Route path="/products" />
        <Route path="/signin">
          <Container className="con" fluid>
            <Login login={login} user={user} logout={logout} error={error} />
          </Container>
        </Route>
        <Route path="/signup">
          <Container className="con" fluid>
            <Signup setUser={setUser} setError={setError} error={error} />
          </Container>
        </Route>
        <Route path="/users">
          <Container className="con" fluid>
            <Users setError={setError} />
          </Container>
        </Route>
        <Route path="/logout">
          <Container className="con" fluid>
            <Logout logout={logout}/>
          </Container>
        </Route>
      </Switch>
    </Router>
  );
}

export default App;
