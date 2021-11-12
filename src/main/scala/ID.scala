package FiveStage
import chisel3._
import chisel3.util.{ BitPat, MuxCase, MuxLookup, Cat, ListLookup }
import chisel3.experimental.MultiIOModule

import lookup._
import Op1Select._
import Op2Select._
import ImmFormat._
import YN._
import DonotCare.DC
import InstructionFetch.Jump


class InstructionDecode extends MultiIOModule {



    val io = IO(new Bundle {
        val in=new Bundle{
            val ins=Input(new Instruction)
            val pc=Input(UInt(30.W))
            val pc_4=Input(UInt(30.W))
        }
        
        val wb_in=Input(new WBRegs.Contents)        
        
        val ex_in=Input(new WBRegs.Contents)
        
        val mem_in=Input(new WBRegs.Contents)
        
        val stall=Output(Bool())
        
        val e_branch=Output(Bool())
        val branch_offset=Output(UInt(30.W))
        
        val out=new Bundle{
            val op_1=Output(UInt(32.W))
            val op_2=Output(UInt(32.W))
            
            val alu_op=Output(UInt(4.W))
            val rd=Output(UInt(5.W))
            val w_rd=Output(UInt(1.W))
            
            val mem_op=Output(Bool())
            val mem_data=Output(UInt(32.W))
            
        }
        
        val jump=Output(new Jump)
        
        
    })

    val registers = Module(new Registers)
    val decoder   = Module(new Decoder)
    
    val reg_rdata_0=Wire(UInt(32.W))
    val reg_rdata_1=Wire(UInt(32.W))
    
    val ins=io.in.ins
    
    val stall=Wire(Bool())
    
    
    reg_rdata_0:=registers.io.readData1
    when(ins.registerRs1===io.mem_in.rd&&io.mem_in.w_rd){
        reg_rdata_0:=io.mem_in.reg_data
    }
    when(ins.registerRs1===io.ex_in.rd&&io.ex_in.w_rd){
        reg_rdata_0:=io.ex_in.reg_data
    }
    
    reg_rdata_1:=registers.io.readData2
    when(ins.registerRs2===io.mem_in.rd&&io.mem_in.w_rd){
        reg_rdata_1:=io.mem_in.reg_data
    }
    when(ins.registerRs2===io.ex_in.rd&&io.ex_in.w_rd){
        reg_rdata_1:=io.ex_in.reg_data
    }
    
    val op_1=reg_rdata_0
    val op_2=reg_rdata_1
    
    val imm_sel = Array(
        ITYPE  -> ins.immediateIType,
        UTYPE  -> ins.immediateUType.asUInt,
        STYPE  -> ins.immediateSType,
    )
    
    val op_1_sel = Array(
        RS1    -> reg_rdata_0,
        PC     -> Cat(io.in.pc,0.U(2.W)),
    )
    
    val imm=MuxLookup(decoder.io.imm_type,1919810.U,imm_sel)
    
    val op_2_sel = Array(
        RS2    -> reg_rdata_1, 
        IMM    -> imm,
        N4     -> 4.U,
    )
    
    val d_pc_sel = Array(
        JALR   -> List(Y,ins.immediateIType + reg_rdata_0),
        // JAL    -> List(Y,io.in.pc+ins.immediateIType)
    )
    
    val no_jump=List(N,0.U)
    
    val jump=ListLookup(
        ins.asUInt,
        no_jump,
        d_pc_sel
    )
    
    val e_branch_sel=Array(
        branchType.eq  ->(op_1===op_2),
        branchType.ne  ->(op_1=/=op_2),
        branchType.ge  ->(op_1.asSInt>op_2.asSInt),
        branchType.lt  ->(op_1.asSInt<op_2.asSInt),
        branchType.geu ->(op_1>op_2),
        branchType.ltu ->(op_1<op_2),
        branchType.jump->false.B
    )
    
    val e_branch=
        MuxLookup(decoder.io.branch_type,false.B,e_branch_sel)&&
        decoder.io.ctrl_signal.branch 
        
    val clear=RegInit(Bool(),false.B)
    clear:=e_branch
    
    val branch_offset=
        io.in.pc+
        ins.immediateBType(12,2).asTypeOf(SInt(30.W)).asUInt
        
    io.e_branch:=e_branch
    io.branch_offset:=branch_offset
    
    io.jump.e_jump:=jump(0)
    io.jump.jump_addr:=jump(1)
    
    
    
    

    
    
    io.out.op_1:=MuxLookup(decoder.io.op_1_type,114514.U,op_1_sel)
    
    io.out.op_2:=MuxLookup(decoder.io.op_2_type,114514.U,op_2_sel)
    
    io.out.mem_data:=reg_rdata_1
    
    registers.io.readAddress1 := ins.registerRs1 
    registers.io.readAddress2 := ins.registerRs2
    registers.io.writeEnable  := io.wb_in.w_rd
    registers.io.writeAddress := io.wb_in.rd 
    registers.io.writeData    := io.wb_in.reg_data 
    
    io.out.alu_op:=decoder.io.alu_op
    
    val nullify=stall|clear
    
    stall:=(
        ((ins.registerRs1===io.ex_in.rd|ins.registerRs2===io.ex_in.rd)&&io.ex_in.mem_op&&io.ex_in.w_rd)|
        ((ins.registerRs1===io.mem_in.rd|ins.registerRs2===io.mem_in.rd)&&io.mem_in.mem_op&&io.mem_in.w_rd)
    )
    // stall:=false.B
    io.out.rd:=Mux(decoder.io.ctrl_signal.regWrite|stall,ins.registerRd,0.U)
        
    io.out.w_rd:=Mux(nullify,false.B,decoder.io.ctrl_signal.regWrite)
    io.out.mem_op:=Mux(nullify,false.B,decoder.io.ctrl_signal.memOp)
    io.stall:=stall
    
    //Warning: add a '&&w_rd' ??
    
    decoder.io.ins := ins    
    // Don't touch the test harness
    val testHarness = IO(
        new Bundle {
            val registerSetup = Input(new RegisterSetupSignals)
            val registerPeek  = Output(UInt(32.W))
            val testUpdates   = Output(new RegisterUpdates)
    })
    testHarness.registerPeek    := registers.io.readData1
    testHarness.testUpdates     := registers.testHarness.testUpdates
    registers.testHarness.setup := testHarness.registerSetup
}



