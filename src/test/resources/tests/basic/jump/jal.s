main:
  j .L1
  li s1,1
  nop
  done
  done
  done
  done
.L1:
  li s1,2
  j .L4
  nop
  nop
  done
.L3:
  li s1,3
  nop
  done
.L4:
  nop
  li s1,4
  j .L3
  done
