package com.lamoroso

import zio.Has

package object example {
  type Writer   = Has[Writer.Service]
  type Listener = Has[Listener.Service]

}
