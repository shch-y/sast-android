package de.mide.pegsolitaire;

import android.content.DialogInterface;
import android.view.View;
import android.view.View.OnClickListener;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import static de.mide.pegsolitaire.model.PlaceStatusEnum.SPACE;
import static de.mide.pegsolitaire.model.PlaceStatusEnum.BLOCKED;
import static de.mide.pegsolitaire.model.PlaceStatusEnum.PEG;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.Space;
import android.widget.Toast;

import de.mide.pegsolitaire.model.PlaceStatusEnum;
import de.mide.pegsolitaire.model.SpacePosition;

public class MainActivity extends AppCompatActivity implements OnClickListener {

    public static final String TAG4LOGGING = "PegSolitaire";

    private static final int TEXT_COLOR_BROWN = 0xffa52a2a;
    private static final int TEXT_COLOR_RED = 0xffff0000;

    /**
     * Unicode字符：实心方块
     */
    private static final String TOKEN_MARK = "■";

    /**
     * 用于存储棋盘初始化的数组。
     */
    private static final PlaceStatusEnum[][] PLACE_INIT_ARRAY =
            {
                    {BLOCKED, BLOCKED, PEG, PEG, PEG, BLOCKED, BLOCKED},
                    {BLOCKED, BLOCKED, PEG, PEG, PEG, BLOCKED, BLOCKED},
                    {PEG, PEG, PEG, PEG, PEG, PEG, PEG},
                    {PEG, PEG, PEG, SPACE, PEG, PEG, PEG},
                    {PEG, PEG, PEG, PEG, PEG, PEG, PEG},
                    {BLOCKED, BLOCKED, PEG, PEG, PEG, BLOCKED, BLOCKED},
                    {BLOCKED, BLOCKED, PEG, PEG, PEG, BLOCKED, BLOCKED}
            };

    private final int _sizeColumn = PLACE_INIT_ARRAY.length;

    private final int _sizeRow = PLACE_INIT_ARRAY[0].length;

    /**
     * 用于存储棋盘上的棋子和空位置的数组。
     */
    private PlaceStatusEnum[][] _placeArray = null;

    /**
     * 当前棋盘上的棋子数量。
     */
    private int _numberOfPegs = -1;
    /**
     * 当前执行的步数。
     */
    private int _numberOfSteps = -1;
    /**
     * 选中的棋子是否已经被移动了。
     */
    private boolean _selectedPegMoved = false;

    /**
     * 用于存储棋盘上的棋子的按钮。
     */
    private ViewGroup.LayoutParams _buttonLayoutParams = null;

    /**
     * 用于开始新游戏的按钮。
     */
    private Button _startButton = null;

    /**
     * 棋盘上的棋子和空位置的布局。
     */
    private GridLayout _gridLayout = null;

    /**
     * 记录当前选中的格子
     */
    private SpacePosition _selected = null;

    /**
     * 用于处理点击棋盘上的棋子的事件。
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.i(TAG4LOGGING, "column=" + _sizeColumn + ", row=" + _sizeRow + "px:");

        _gridLayout = findViewById(R.id.boardGridLayout);

        displayResolutionEvaluate();
        actionBarConfiguration();
        initializeBoard();
    }

    /**
     * 从显示器读取分辨率并将值写入适当的成员变量。
     */
    private void displayResolutionEvaluate() {

        WindowManager windowManager = getWindowManager();
        Display display = windowManager.getDefaultDisplay();

        DisplayMetrics displayMetrics = new DisplayMetrics();
        display.getMetrics(displayMetrics);

        int displayWidth = displayMetrics.widthPixels;
        int displayHeight = displayMetrics.heightPixels;

        Log.i(TAG4LOGGING, "Display-Resolution: " + displayWidth + "x" + displayHeight);

        int _sideLengthPlace = displayWidth / _sizeColumn;

        _buttonLayoutParams = new ViewGroup.LayoutParams(_sideLengthPlace,
                _sideLengthPlace);
    }

    /**
     * 初始化操作栏。
     */
    private void actionBarConfiguration() {

        ActionBar actionBar = getSupportActionBar();
        if (actionBar == null) {

            Toast.makeText(this, "没有操作栏", Toast.LENGTH_LONG).show();
            return;
        }

        actionBar.setTitle("单人跳棋");
    }

    /**
     * 从资源文件加载操作栏菜单项。
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.action_bar_menu_items, menu);

        return super.onCreateOptionsMenu(menu);
    }

    /**
     * 处理操作栏菜单项的选择。
     * 在扩展的版本中，你需要加入更多的菜单项。
     *
     * @param item 选择的菜单项
     * @return true: 选择的菜单项被处理了
     * false: 选择的菜单项没有被处理
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.action_new_game) {

            selectedNewGame();
            return true;

        } else
            return super.onOptionsItemSelected(item);
    }

    /**
     * 处理点击"新游戏"按钮的事件。
     * 弹出对话框，询问用户是否要开始新游戏。
     * 如果用户选择"是"，则初始化棋盘，否则不做任何事情。
     */
    public void selectedNewGame() {
        //TODO || DONE
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setTitle("询问");
        dialogBuilder.setMessage("是否开始新游戏");
        dialogBuilder.setPositiveButton("是", (dialogInterface, i) -> {
            initializeBoard();
        });
        dialogBuilder.setNegativeButton("否", (dialogInterface, i) -> {});

        AlertDialog dialog = dialogBuilder.create();
        dialog.show();

    }


    /**
     * 初始化棋盘上的棋子和空位置。
     */
    private void initializeBoard() {

        if (_gridLayout.getRowCount() == 0) {

            _gridLayout.setColumnCount(_sizeRow);

        } else { // 清除旧的棋盘

            _gridLayout.removeAllViews();
        }

        _numberOfSteps = 0;
        _numberOfPegs = 0;
        _selectedPegMoved = false;
        _placeArray = new PlaceStatusEnum[_sizeColumn][_sizeRow];

        for (int i = 0; i < _sizeColumn; i++) {

            for (int j = 0; j < _sizeRow; j++) {

                PlaceStatusEnum placeStatus = PLACE_INIT_ARRAY[i][j];

                _placeArray[i][j] = placeStatus;

                switch (placeStatus) {

                    case PEG:
                        generateButton(i, j, true);
                        break;

                    case SPACE:
                        generateButton(i, j, false);
                        break;

                    case BLOCKED:
                        Space space = new Space(this); // Dummy-Element
                        _gridLayout.addView(space);
                        break;

                    default:
                        Log.e(TAG4LOGGING, "错误的棋盘状态");

                }
            }
        }

        Log.i(TAG4LOGGING, "棋盘初始化完成");
        updateDisplayStepsNumber();
    }

    /**
     * 生成棋盘上的一个位置。
     * 在基础任务中，棋盘上的棋子直接用字符 TOKEN_MARK 表示。
     * 在扩展任务中，棋盘上的棋子用图片表示。
     */
    private void generateButton(int indexColumn, int indexRow, boolean isPeg) {

        Button button = new Button(this);

        button.setTextSize(22.0f);
        button.setLayoutParams(_buttonLayoutParams);
        button.setOnClickListener(this);
        button.setTextColor(TEXT_COLOR_BROWN);

        SpacePosition pos = new SpacePosition(indexColumn, indexRow);
        button.setTag(pos);

        // TODO || DONE
        if (isPeg) button.setText(TOKEN_MARK);
        else button.setText(" ");

        _gridLayout.addView(button);


    }


    /**
     * 更新操作栏中的步数显示。
     */
    private void updateDisplayStepsNumber() {

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setSubtitle("执行步数：" + _numberOfSteps);
        }
    }

    /**
     * 处理棋盘上的点击事件。
     * 如果被点击的按钮是一个棋子，那么它将被改变选中状态。
     * 也就是说，如果它之前没有被选中，这个棋子会变为红色，
     * 同时，此前被选中的棋子（如果有）将变为棕色。
     * 或者如果它已经被选中，那么它自己将变为棕色。
     * 如果被点击的按钮是一个空位置，那么试图将被选中的棋子移动到该位置。
     * 如果移动成功，你需要更新棋盘上的棋子和空位置。
     * 如果移动失败，你需要显示一个错误信息。
     *
     * @param view 被点击的按钮
     *

     */
    @Override
    public void onClick(View view) {

        Button clickedButton = (Button) view;

        SpacePosition targetPosition = (SpacePosition) clickedButton.getTag();

        // 获取被点击的按钮的位置
        int indexColumn = targetPosition.getIndexColumn();
        int indexRow = targetPosition.getIndexRow();
        PlaceStatusEnum placeStatus = _placeArray[indexColumn][indexRow];

        switch (placeStatus) {

            case PEG:
                // TODO || DONE
                if (clickedButton.getCurrentTextColor()==TEXT_COLOR_RED){//如果之前被选中？，那么...
                    clickedButton.setTextColor(TEXT_COLOR_BROWN);
                    _selected = null;
                }
                else {//如果之前没有被选中，那么...

                    if (_selected!=null){
                    Button preselected = getButtonFromPosition(_selected);
                    preselected.setTextColor(TEXT_COLOR_BROWN);
                    }
                    clickedButton.setTextColor(TEXT_COLOR_RED);
                    _selected = (SpacePosition) clickedButton.getTag();

                }

                break;

            case SPACE:
                // TODO || DONE
                if (_selected==null) {
                    Log.e(TAG4LOGGING, "未选中棋子，无法移动");
                    break;
                }

                SpacePosition start = _selected;
                SpacePosition end = (SpacePosition) clickedButton.getTag();

                SpacePosition skipped = getSkippedPosition(start,end);
                if (skipped==null) {
                    Log.e(TAG4LOGGING,"不合法的跳棋请求" + "start: " + start + "end: " + end);
                    break;
                }
                jumpToPosition(getButtonFromPosition(start),getButtonFromPosition(end),getButtonFromPosition(skipped));
                break;

            default:
                Log.e(TAG4LOGGING, "错误的棋盘状态" + placeStatus);
        }
    }

    /**
     * 执行跳跃。仅当确定移动合法时才可以调用该方法。
     * 数组中三个位置的状态，和总棋子数发生变化。
     * 同时，在移动后，你需要检查是否已经结束游戏。
     *
     * @param startButton 被选中的棋子
     * @param targetButton 被选中的空位置
     * @param skippedButton 被跳过的棋子
     *
     */
    private void jumpToPosition(Button startButton, Button targetButton, Button skippedButton) {

        // TODO || DONE

        startButton.setTextColor(TEXT_COLOR_BROWN);
        startButton.setText(" ");

        targetButton.setTextColor(TEXT_COLOR_RED);
        targetButton.setText(TOKEN_MARK);
        _selected = (SpacePosition) targetButton.getTag();

        skippedButton.setText(" ");

        ChangeStatus(startButton,SPACE);
        ChangeStatus(targetButton,PEG);
        ChangeStatus(skippedButton,SPACE);


        _numberOfSteps++;
        _numberOfPegs--;
        updateDisplayStepsNumber();
        if (_numberOfPegs == 1) {
            showVictoryDialog();
        }
        else if (!has_movable_places()) {
            showFailureDialog();
        }
    }

    /** 改变指定位置棋子状态
     *
     */

    private void ChangeStatus(Button bu,PlaceStatusEnum sta){
        SpacePosition pos = (SpacePosition) bu.getTag();

        int Column = pos.getIndexColumn();
        int Row = pos.getIndexRow();
        _placeArray[Column][Row] = sta;
    }

    /** 获得指定位置棋子状态
     *
     */

    private PlaceStatusEnum FindStatus(Button bu){
        SpacePosition pos = (SpacePosition) bu.getTag();
        int Column = pos.getIndexColumn();
        int Row = pos.getIndexRow();
        return _placeArray[Column][Row];
    }
    private PlaceStatusEnum FindStatusByPos(SpacePosition pos){
        int Column = pos.getIndexColumn();
        int Row = pos.getIndexRow();
        return _placeArray[Column][Row];
    }



    /**
     * 返回位置对应的按钮。
     *
     * @param position 位置
     * @return 按钮
     */
    private Button getButtonFromPosition(SpacePosition position) {

        int index = position.getPlaceIndex(_sizeRow);

        return (Button) _gridLayout.getChildAt(index);
    }

    /**
     * 显示一个对话框，表明游戏已经胜利（只剩下一个棋子）。
     * 点击对话框上的按钮，可以重新开始游戏。
     * 在扩展版本中，你需要在这里添加一个输入框，让用户输入他的名字。
     */
    private void showVictoryDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setTitle("胜利");
        dialogBuilder.setMessage("你赢了！");
        dialogBuilder.setPositiveButton("再来一局", (dialogInterface, i) -> {
            initializeBoard();  // 重新开始游戏
        });

        AlertDialog dialog = dialogBuilder.create();
        dialog.show();
    }

    /**
     * 显示一个对话框，表明游戏已经失败（没有可移动的棋子）。
     * 点击对话框上的按钮，可以重新开始游戏。
     */
    private void showFailureDialog(){
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setTitle("失败");
        dialogBuilder.setMessage("你输了！");
        dialogBuilder.setPositiveButton("再来一局", (dialogInterface, i) -> {
            initializeBoard();  // 重新开始游戏
        });

        AlertDialog dialog = dialogBuilder.create();
        dialog.show();
    }

    /**
     * 给定一个起始位置和目标位置。
     * 如果移动合法，返回被跳过的位置。
     * 如果移动不合法，返回 {@code null}。
     * 移动合法的定义，参见作业文档。
     *
     * @param startPos  起始位置
     * @param targetPos 目标位置
     * @return 移动合法时，返回一个新{@code SpacePosition}
     * 表示被跳过的位置；否则返回 {@code null}
     */
    private SpacePosition getSkippedPosition(SpacePosition startPos, SpacePosition targetPos) {
        // TODO || DONE


        int startCol = startPos.getIndexColumn();
        int startRow = startPos.getIndexRow();
        int targetCol = targetPos.getIndexColumn();
        int targetRow = targetPos.getIndexRow();


        if (FindStatusByPos(startPos) != PEG || FindStatusByPos(targetPos) != SPACE) return null;
        if (startRow==targetRow && Math.abs(startCol-targetCol)==2){
            SpacePosition skipped = new SpacePosition((startCol+targetCol)/2,startRow);
            Button skippedButton = getButtonFromPosition(skipped);
            if (FindStatus(skippedButton)==PEG) return skipped;
        }
        else if (startCol==targetCol && Math.abs(startRow-targetRow)==2){
            SpacePosition skipped = new SpacePosition(startCol,(startRow+targetRow)/2);
            Button skippedButton = getButtonFromPosition(skipped);
            if (FindStatus(skippedButton)==PEG) return skipped;
        }

        return null;
    }

    /**
     * 返回是否还有可移动的位置。
     *
     * @return 如果还有可移动的位置，返回 {@code true}
     * 否则返回 {@code false}
     */
    private Boolean has_movable_places(){
        for(int i = 0; i < _sizeColumn; i++){
            for(int j = 0; j < _sizeRow; j++){
                if(_placeArray[i][j] == PEG){
                    // TODO
                    SpacePosition cur = new SpacePosition(i,j);

                    SpacePosition rowp2 = new SpacePosition(i,j+2);
                    SpacePosition rowm2 = new SpacePosition(i,j-2);
                    SpacePosition colp2 = new SpacePosition(i+2,j);
                    SpacePosition colm2 = new SpacePosition(i-2,j);

                    if (j+2<_sizeRow&&getSkippedPosition(cur,rowp2)!= null)  return true;
                    else if (j-2>=0 && getSkippedPosition(cur,rowm2)!=null) return true;
                    else if (i+2<_sizeColumn && getSkippedPosition(cur,colp2)!=null) return true;
                    else if (i-2>=0 && getSkippedPosition(cur,colm2)!=null) return true;

                }
            }
        }
        return false;
    }
}
