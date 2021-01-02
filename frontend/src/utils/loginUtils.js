import facade from "../facades/LoginFacade";
import { getUserByJwt, setToken } from "./token";

export const loginMethod = (user, pass, setUser) => {
  facade
    .login(user, pass)
    .then((res) => {
      setToken(res.token);
      setUser({ ...getUserByJwt() });
    })
    .catch((err) => {
      if (err.status) {
        err.fullError.then((e) => {
          setUser(e.message);
        });
      } else {
        console.log("Network error");
      }
    });
};

export const logoutMethode = (setUser, init) => {
  setUser({...init});
  localStorage.removeItem("jwtToken")
};



