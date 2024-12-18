import React from 'react'
import { FadeLoader, ScaleLoader } from 'react-spinners'

const Spinner = () => {
  return (
    <div className='fixed inset-0 flex items-center justify-center bg-gray-500 bg-opacity-50 z-50'>
      <ScaleLoader color='black' />
    </div>
  )
}

export default Spinner