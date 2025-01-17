// BoardMapper.java
package com.thegoalgrid.goalgrid.mapper;

import com.thegoalgrid.goalgrid.dto.board.BoardDTO;
import com.thegoalgrid.goalgrid.entity.Board;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class BoardMapper {

    private final ModelMapper modelMapper;

    public BoardMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public BoardDTO toDTO(Board board) {
        BoardDTO boardDTO = modelMapper.map(board, BoardDTO.class);
        boardDTO.setOwnerId(board.getOwner().getId());
        return boardDTO;
    }

    public Board toEntity(BoardDTO boardDTO) {
        return modelMapper.map(boardDTO, Board.class);
    }
}
