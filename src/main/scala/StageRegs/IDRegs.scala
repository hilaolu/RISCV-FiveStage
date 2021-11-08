package FiveStage
import chisel3._
import chisel3.experimental.MultiIOModule
import chisel3.util._

import FiveStage.AluOp._

class EXRegs extends MultiIOModule {

  val io = IO(
    new Bundle {
        val in=new Bundle{
            val op_0 = Input(UInt(32.W)) 
            val op_1 = Input(UInt(32.W)) 
            val alu_op= Input(UInt(4.W))
            val rd= Input(UInt(5.W))
            val w_rd=Input(UInt(1.W))
        }
        
        val out=new Bundle{
            val op_0 = Output(UInt(32.W)) 
            val op_1 = Output(UInt(32.W)) 
            val alu_op= Output(UInt(4.W))
            val rd= Output(UInt(5.W))
            val w_rd=Output(UInt(1.W))
        }
    })
    
    val op_0=Reg(UInt(32.W))
    val op_1=Reg(UInt(32.W))
    val alu_op=Reg(UInt(4.W))
    val rd=Reg(UInt(5.W))
    val w_rd=RegInit(UInt(1.W),0.U)
    
    io.out:=DontCare
    

}
