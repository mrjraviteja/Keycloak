import { useState } from 'react'
import { httpClient } from './HttpClient'
import reactLogo from './assets/react.svg'
import keycloakLogo from './assets/keycloak.png'
import Keycloak from 'keycloak-js'
import './App.css'

const initOptions = {
  url: 'http://localhost:8080/',
  realm: "TestIDP",
  clientId: "test-idp"
}

const kc = new Keycloak(initOptions)

kc.init({
  onLoad: 'login-required',
  checkLoginIframe: true,
  pkceMethod: 'S256'
}).then((auth) => {
  if(!auth)
  {
    console.log('Authentication falied. Reloading...')
  }
  else
  {
    console.info('Authenticated')
    console.log('auth',auth)
    console.log('Keycloak',kc)

    httpClient.defaults.headers.common['Authorization'] = `Bearer ${kc.token}`
    

    kc.onTokenExpired = () => {
      console.log('Token expired')
    }
  }
}, () => {
  console.error("Authentication Failed")
})

function App() {


  return (
    <>
    <h1>Welcome user!</h1>
    <button id="btn" onClick={() => {kc.logout({redirectUri: "http://localhost:5173"})}}>Logout</button>
    </>
  )
}

export default App
