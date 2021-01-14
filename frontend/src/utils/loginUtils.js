import facade from "../facades/LoginFacade";
import { getUserByJwt, setToken, setCount } from "./token";

export const loginMethod = (user, pass, setUser, setError) => {
  facade
    .login(user, pass)
    .then((res) => {
      console.log(res.count)
      setToken(res.token);
      setCount(res.count);
      setUser({ ...getUserByJwt() });
      setError("")
    })
    .catch((err) => {
      if (err.status) {
        err.fullError.then((e) => {
          setError(e.message);
        });
      } else {
        console.log("Network error");
      }
    });
};

export const logoutMethode = (setUser, init) => {
  setUser({...init});
  localStorage.removeItem("jwtToken")
  localStorage.removeItem("count")

};



