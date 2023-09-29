let localVideo = document.getElementById("local-video")
let remoteVideo = document.getElementById("remote-video")

localVideo.style.opacity = 0
remoteVideo.style.opacity = 0

localVideo.onplaying = () => { localVideo.style.opacity = 1 }
remoteVideo.onplaying = () => { remoteVideo.style.opacity = 1 }

//-------------------------------------------------------------------------------------------------------


let localStream;
let activeCall;

function init(userId) {
  peer = new Peer(userId, {
    port: 443,
    path: '/'
  })

  peer.on('open', () => {
    Android.onPeerConnected()
  })

  listenForCalls()
  listenForDisconnect()
  endCall()
}

function listenForCalls() {
  peer.on('call', (call) => {
    navigator.getUserMedia({
      audio: true,
      video: true
    }, (stream) => {
      localStream = stream
      localVideo.srcObject = stream

      call.answer(stream)
      activeCall = call

      call.on('stream', (remoteStream) => {
        remoteVideo.srcObject = remoteStream

        remoteVideo.className = "primary-video"
        localVideo.className = "secondary-video"
      })

      call.on('close', () => {
        endCall()
      })
    })
  })
}

function listenForDisconnect() {
  peer.on('disconnected', () => {
    endCall()
  })

  peer.on('close', () => {
    endCall()
  })
}

function startCall(otherUserId) {
  navigator.getUserMedia({
    audio: true,
    video: true
  }, (stream) => {
    localStream = stream
    localVideo.srcObject = stream

    const call = peer.call(otherUserId, stream)
    activeCall = call

    call.on('stream', (remoteStream) => {
      remoteVideo.srcObject = remoteStream

      remoteVideo.className = "primary-video"
      localVideo.className = "secondary-video"
    })

    call.on('close', () => {
      endCall()
    })
  })
}

function toggleVideo(b) {
  if (b == "true") {
    localStream.getVideoTracks()[0].enabled = true
  } else {
    localStream.getVideoTracks()[0].enabled = false
  }
}

function toggleAudio(b) {
  if (b == "true") {
    localStream.getAudioTracks()[0].enabled = true
  } else {
    localStream.getAudioTracks()[0].enabled = false
  }
}

function endCall() {
  if (activeCall) {
    activeCall.close()
  }

  if (localStream) {
    localStream.getTracks().forEach((track) => {
      track.stop()
    })
  }

  localVideo.srcObject = null
  remoteVideo.srcObject = null

}



