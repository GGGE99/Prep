import React, { useEffect, useState } from "react";
import Navbar from "./components/Navbar";
import { BrowserRouter as Router, Switch, Route } from "react-router-dom";
import { Container } from "react-bootstrap";
import { Jokes, Signup, Login, Home, Users } from "./components";
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
    <Container className="con" fluid>
      <Router>
        <Navbar user={user} logout={logout} />
        <Switch>
          <Route path="/" exact>
            <Home />
          </Route>
          <Route path="/jokes">
            <Jokes />
          </Route>
          <Route path="/products" />
          <Route path="/signin">
            <Login login={login} user={user} logout={logout} error={error} />
          </Route>
          <Route path="/signup">
            <Signup setUser={setUser} setError={setError} error={error} />
          </Route>
          <Route path="/users">
            <Users setError={setError} />
          </Route>
        </Switch>
      </Router>
    </Container>
  );
}

export default App;
