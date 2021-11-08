package FiveStage
import chisel3._
import chisel3.experimental.MultiIOModule
import chisel3.util._

import FiveStage.AluOp._

class Execute extends MultiIOModule {

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
            val reg_w_data=Output(UInt(32.W))
            val rd=Output(UInt(5.W))
            val w_rd=Output(UInt(1.W))
        }
    })
    
    val ALUopMap = Array(
      ADD    -> (io.in.op_0 + io.in.op_1),
      SUB    -> (io.in.op_0 - io.in.op_1),
    )
    
    io.out.reg_w_data := MuxLookup(io.in.alu_op, 0.U(32.W), ALUopMap)
    io.out.rd:=io.in.rd
    io.out.w_rd:=io.in.w_rd

}
