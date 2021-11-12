package FiveStage
import chisel3._
import chisel3.experimental.MultiIOModule
import chisel3.util._

import FiveStage.AluOp._

class Execute extends MultiIOModule {

  val io = IO(
    new Bundle {
        val in=new Bundle{
            val op_1 = Input(UInt(32.W)) 
            val op_2 = Input(UInt(32.W)) 
            val alu_op= Input(UInt(4.W))
            val rd= Input(UInt(5.W))
            val w_rd=Input(UInt(1.W))
            
            val mem_op=Input(Bool())
            val mem_data=Input(UInt(32.W))
        }
        
        val out=new Bundle{
            val alu_data=Output(UInt(32.W))
            val rd=Output(UInt(5.W))
            val w_rd=Output(UInt(1.W))
            
            val mem_op=Output(Bool())
            val mem_data=Output(UInt(32.W))
        }
    })
    
    val ALUopMap = Array(
        ADD    -> (io.in.op_1 + io.in.op_2),
        SUB    -> (io.in.op_1 - io.in.op_2), //todo use complement code
        AND    -> (io.in.op_1 & io.in.op_2),
        OR     -> (io.in.op_1 | io.in.op_2),
        XOR    -> (io.in.op_1 ^ io.in.op_2),
        SLTU   -> (io.in.op_1 < io.in.op_2),
        SLT    -> (io.in.op_1.asSInt < io.in.op_2.asSInt),
        SRA    -> (io.in.op_1.asSInt >> io.in.op_2(4,0)).asUInt,
        SRL    -> (io.in.op_1 >> io.in.op_2(4,0)),
        SLL    -> (io.in.op_1 << io.in.op_2(4,0)),
        COPY_B -> (io.in.op_2),
        COPY_A -> (io.in.op_1),
    )
    
    io.out.alu_data := MuxLookup(io.in.alu_op, 0.U(32.W), ALUopMap)
    io.out.rd:=io.in.rd
    io.out.w_rd:=io.in.w_rd
    
    io.out.mem_op:=io.in.mem_op
    io.out.mem_data:=io.in.mem_data

}
