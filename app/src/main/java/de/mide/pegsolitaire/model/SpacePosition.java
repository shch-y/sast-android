package de.mide.pegsolitaire.model;

import androidx.annotation.NonNull;

/**
 * 表示棋盘上的一个位置，可以是一个棋子或者空位置。
 */
public class SpacePosition {

    private int _indexColumn = -1;

    private int _indexRow = -1;

    /**
     * 构造函数
     *
     * @param indexColumn 列索引
     * @param indexRow 行索引
     */
    public SpacePosition(int indexColumn, int indexRow) {

        _indexColumn = indexColumn;
        _indexRow = indexRow;
    }

    /**
     * 返回列索引。
     * @return 列索引，从0开始
     */
    public int getIndexColumn() {

        return _indexColumn;
    }

    /**
     * 返回行索引。
     * @return 行索引，从0开始
     */
    public int getIndexRow() {

        return _indexRow;
    }

    /**
     * 返回位置的索引，从0开始。
     *
     * @param sizeOfBoard 棋盘的边长
     * @return 位置的索引，从0开始
     */
    public int getPlaceIndex(int sizeOfBoard) {

        return sizeOfBoard* _indexColumn + _indexRow;
    }

    @NonNull
    @Override
    public String toString() {

        return "SpacePosition: column=" + _indexColumn + ", row=" + _indexRow;
    }

}
