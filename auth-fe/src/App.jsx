import { useState } from 'react'
import reactLogo from './assets/react.svg'
import viteLogo from '/vite.svg'
import './App.css'
import { Route, Routes } from 'react-router-dom'
import LoginPage from './pages/LoginPage'
import SignUp from './pages/SignUp'
import PrivateRoute from './utils/PrivateRoute'
import Home from './pages/Home'

function App() {
  const [count, setCount] = useState(0)

  return (
    <Routes>
      <Route path='/' element={<LoginPage/>}/>
      <Route path='/signup' element={<SignUp/>}/>
      <Route path='/home' element={<PrivateRoute><Home/></PrivateRoute>}/>
    </Routes>
  )
}

export default App
