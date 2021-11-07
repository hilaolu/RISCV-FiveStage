package FiveStage
import chisel3._
import chisel3.experimental.MultiIOModule
import chisel3.util._

import FiveStage.ALUOps._

class Execute extends MultiIOModule {

  // Don't touch
  // val testHarness = IO(
  //   new Bundle {
  //     val IMEMsetup = Input(new IMEMsetupSignals)
  //     val PC        = Output(UInt())
  //   }
  // )


  val io = IO(
    new Bundle {
        val op_0 = Input(UInt(32.W)) 
        val op_1 = Input(UInt(32.W)) 
        val alu_op= Input(UInt(4.W))
        val dst= Input(UInt(5.W))
        
        val wdata=Output(UInt(32.W))
        val waddr=Output(UInt(5.W))
    })
    
    val ALUopMap = Array(
      ADD    -> (io.op_0 + io.op_1),
      SUB    -> (io.op_0 - io.op_1),
    )
    
    io.wdata := MuxLookup(io.alu_op, 0.U(32.W), ALUopMap)
    io.waddr:=io.dst

}
