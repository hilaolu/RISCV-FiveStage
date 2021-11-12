package FiveStage
import chisel3._
import chisel3.experimental.MultiIOModule
import chisel3.util._

import FiveStage.AluOp._

object EXRegs {
    class Contents extends Bundle{
        val op_1 = UInt(32.W) 
        val op_2 = UInt(32.W) 
        val alu_op= UInt(4.W)
        val rd= UInt(5.W)
        val w_rd=UInt(1.W)
        
        val mem_op=Bool()
        val mem_data=UInt(32.W)
    }
}

class EXRegs extends MultiIOModule {

  val io = IO(
    new Bundle {
        val in=Input(new EXRegs.Contents)        
        val out=Output(new EXRegs.Contents)
    })
    
    val contents=Reg(new EXRegs.Contents)
    
    contents:=io.in
    io.out:=contents
    

}
