package com.zzqs.app_kc.adapter;

import android.content.Context;
import android.graphics.Color;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.zzqs.app_kc.R;
import com.zzqs.app_kc.activities.ActualDeliveryActivity;
import com.zzqs.app_kc.entity.Events;
import com.zzqs.app_kc.utils.StringTools;

import java.util.ArrayList;

import de.greenrobot.event.EventBus;

/**
 * 类名：ActualDeliveryAdapter
 * 描述：实际交货页面适配器
 * Created by ray on 15/12/10.
 */
public class ActualDeliveryAdapter extends BaseAdapter {
  private Context context;
  private ArrayList<ActualDeliveryActivity.Goods> list, operationList;
  private Animation begin;
  private Animation close;

  public ActualDeliveryAdapter(Context context, ArrayList<ActualDeliveryActivity.Goods> list, ArrayList<ActualDeliveryActivity.Goods> operationList) {
    this.context = context;
    this.list = list;
    this.operationList = operationList;
    begin = AnimationUtils.loadAnimation(context, R.anim.animation_actual_begin);
    close = AnimationUtils.loadAnimation(context, R.anim.animation_actual_close);
  }

  public void setOperationList(ArrayList<ActualDeliveryActivity.Goods> operationList) {
    this.operationList = operationList;
    notifyDataSetChanged();
  }

  @Override
  public int getCount() {
    if (operationList == null)
      return 0;
    return operationList.size();
  }

  @Override
  public Object getItem(int i) {
    return operationList.get(i);
  }

  @Override
  public long getItemId(int i) {
    return i;
  }

  @Override
  public View getView(final int i, View view, ViewGroup viewGroup) {
    if (i > operationList.size()) {
      return null;
    }
    ViewHolder holder;
    ActualDeliveryActivity.Goods goods = operationList.get(i);//操作数据
    ActualDeliveryActivity.Goods referGoods = list.get(i);//参考数据
    if (view == null) {
      view = LayoutInflater.from(context).inflate(R.layout.item_actual_delivert, null);
      holder = new ViewHolder();
      holder.tv_goods_name = (TextView) view.findViewById(R.id.tv_goods_name);
      holder.et_quantity = (EditText) view.findViewById(R.id.et_quantity);
      holder.et_quantity.setTag(i);
      holder.et_quantity.setFocusable(false);
      holder.btn_add = (Button) view.findViewById(R.id.btn_add);
      holder.btn_cut = (Button) view.findViewById(R.id.btn_cut);
      holder.tv_unit = (TextView) view.findViewById(R.id.tv_unit);
      holder.btn_lack = (Button) view.findViewById(R.id.btn_lack);
      holder.btn_damage = (Button) view.findViewById(R.id.btn_damage);
      holder.ll_damageType = (LinearLayout) view.findViewById(R.id.ll_damageType);
      holder.et_quantity.addTextChangedListener(new MyTextWatcher(holder) {

        @Override
        public void afterTextChanged(Editable s, final ViewHolder holder) {
          int position = (int) holder.et_quantity.getTag();
          ActualDeliveryActivity.Goods goods = operationList.get(position);
          ActualDeliveryActivity.Goods referGoods = list.get(position);
          String count = holder.et_quantity.getText().toString();
          if (StringTools.isEmp(count)) {
            Toast.makeText(context, R.string.prompt_must_input_content, Toast.LENGTH_SHORT).show();
            return;
          } else if (!StringTools.isNumber(count)) {
            Toast.makeText(context, R.string.prompt_must_input_num, Toast.LENGTH_SHORT).show();
            holder.et_quantity.setTextColor(context.getResources().getColor(R.color.red));
            return;
          } else if (!count.contains(".")) {//是整数
            if (Double.parseDouble(count) > 0) {
              if (Double.parseDouble(count) > Double.parseDouble(referGoods.getCount())) {
                Toast.makeText(context, R.string.prompt_is_limit, Toast.LENGTH_SHORT).show();
                holder.et_quantity.setText(referGoods.getCount());
                return;
              } else {
                if (holder.ll_damageType.getVisibility() == View.GONE) {//没显示才显示
                  holder.ll_damageType.startAnimation(begin);
                  holder.ll_damageType.setVisibility(View.VISIBLE);
                }
              }
              goods.setCount(count + "");
              operationList.set(position, goods);
              if (goods.getHasLack().equals("true")) {
                moneyCount();
              }
            } else {
              goods.setHasLack("false");
              holder.btn_lack.setBackgroundColor(context.getResources().getColor(R.color.second_bg_gray));
              holder.btn_lack.setTextColor(context.getResources().getColor(R.color.actual_text));
              goods.setHasDamage("false");
              holder.btn_damage.setBackgroundColor(context.getResources().getColor(R.color.second_bg_gray));
              holder.btn_damage.setTextColor(context.getResources().getColor(R.color.actual_text));
              holder.ll_damageType.startAnimation(close);
              close.setAnimationListener(new Animation.AnimationListener() {//动画结束后才隐藏控件
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                  holder.ll_damageType.setVisibility(View.GONE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
              });
              goods.setCount(count + "");
              operationList.set(position, goods);
              moneyCount();
            }
            holder.et_quantity.setTextColor(context.getResources().getColor(R.color.text_black));
          } else if (count.contains(".")) {//是小数
            if (Float.parseFloat(count) > 0) {
              if (Float.parseFloat(count) > Float.parseFloat(referGoods.getCount())) {
                Toast.makeText(context, R.string.prompt_is_limit, Toast.LENGTH_SHORT).show();
                holder.et_quantity.setText(referGoods.getCount());
              } else {
                if (holder.ll_damageType.getVisibility() == View.GONE) {//没显示才显示
                  holder.ll_damageType.startAnimation(begin);
                  holder.ll_damageType.setVisibility(View.VISIBLE);
                }
              }
              goods.setCount(count + "");
              operationList.set(position, goods);
              if (goods.getHasLack().equals("true")) {
                moneyCount();
              }
            } else {
              goods.setHasLack("false");
              holder.btn_lack.setBackgroundColor(context.getResources().getColor(R.color.second_bg_gray));
              holder.btn_lack.setTextColor(context.getResources().getColor(R.color.actual_text));
              goods.setHasDamage("false");
              holder.btn_damage.setBackgroundColor(context.getResources().getColor(R.color.second_bg_gray));
              holder.btn_damage.setTextColor(context.getResources().getColor(R.color.actual_text));
              holder.ll_damageType.startAnimation(close);
              close.setAnimationListener(new Animation.AnimationListener() {//动画结束后才隐藏控件
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                  holder.ll_damageType.setVisibility(View.GONE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
              });
              goods.setCount(count + "");
              operationList.set(position, goods);
              moneyCount();
            }
            holder.et_quantity.setTextColor(context.getResources().getColor(R.color.text_black));
          }
        }
      });
      view.setTag(holder);
    } else {
      holder = (ViewHolder) view.getTag();
      holder.et_quantity.setTag(i);
    }

    if (!StringTools.isEmp(goods.getName())) {
      String count1 = "", count2 = "", count3 = "", unit1 = "", unit2 = "", unit3 = "";
      if (!StringTools.isEmp(referGoods.getCount())) {
          count1 = referGoods.getCount();
          unit1 = referGoods.getUnit();
      }

      if (!StringTools.isEmp(referGoods.getCount2())) {
          count2 = referGoods.getCount2();
          unit2 = referGoods.getUnit2();
      }

      if (!StringTools.isEmp(referGoods.getCount3())) {
          count3 = referGoods.getCount3();
          unit3 = referGoods.getUnit3();
      }

      holder.tv_goods_name.setText(referGoods.getName() + "  " + count1 + " " + unit1 + "  " + count2 + " " + unit2 + "  " + count3 + " " + unit3);
    }

    if (StringTools.isEmp(referGoods.getCount()) || referGoods.getCount().equals("0")) {
      holder.btn_add.setEnabled(false);
      holder.btn_cut.setEnabled(false);
      holder.et_quantity.setEnabled(false);
      holder.et_quantity.setTextColor(context.getResources().getColor(R.color.hint));
    } else {
      holder.btn_add.setEnabled(true);
      holder.btn_cut.setEnabled(true);
      holder.et_quantity.setEnabled(true);
    }
    if (!StringTools.isEmp(goods.getCount()) && !goods.getCount().equals("0")) {//说明之前修改过实际数量
      String quantity = goods.getCount() + "";
      holder.et_quantity.setText(quantity);
      moneyCount();
      holder.ll_damageType.setVisibility(View.VISIBLE);
      if (goods.getHasLack().equals("true")) {
        holder.btn_lack.setBackgroundColor(context.getResources().getColor(R.color.red_live_4));
        holder.btn_lack.setTextColor(Color.WHITE);
      }
      if (goods.getHasDamage().equals("true")) {
        holder.btn_damage.setBackgroundColor(context.getResources().getColor(R.color.red_live_4));
        holder.btn_damage.setTextColor(Color.WHITE);
      }
    } else {
      holder.et_quantity.setText(0 + "");
      holder.ll_damageType.setVisibility(View.GONE);
      if (!goods.getHasLack().equals("true")) {
        holder.btn_lack.setBackgroundColor(context.getResources().getColor(R.color.second_bg_gray));
        holder.btn_lack.setTextColor(context.getResources().getColor(R.color.actual_text));
      }
      if (!goods.getHasDamage().equals("true")) {
        holder.btn_damage.setBackgroundColor(context.getResources().getColor(R.color.second_bg_gray));
        holder.btn_damage.setTextColor(context.getResources().getColor(R.color.actual_text));
      }
    }

    holder.tv_unit.setText(goods.getUnit());

    holder.et_quantity.setSelection(holder.et_quantity.getText().length());
    holder.btn_add.setOnClickListener(new BtnListener(holder, i, BtnListener.ADD1));

    holder.btn_cut.setOnClickListener(new BtnListener(holder, i, BtnListener.CUT1));

    holder.btn_lack.setOnClickListener(new BtnListener(holder, i, BtnListener.LACK1));

    holder.btn_damage.setOnClickListener(new BtnListener(holder, i, BtnListener.DAMAGE1));

    return view;
  }

  public ArrayList<ActualDeliveryActivity.Goods> getList() {
    return operationList;
  }

  private void moneyCount() {
    EventBus.getDefault().post(new Events.GoodsEvent(Events.CUT_MONEY));
  }

  private class ViewHolder {
    EditText et_quantity;
    Button btn_cut, btn_add, btn_lack, btn_damage;
    TextView tv_unit, tv_goods_name;
    LinearLayout ll_damageType;
  }

  private class BtnListener implements View.OnClickListener {
    ViewHolder holder;
    int position;
    int type;
    public static final int ADD1 = 1;
    //        public static final int ADD2 = 2;
    public static final int CUT1 = 3;
    //        public static final int CUT2 = 4;
    public static final int LACK1 = 5;
    //        public static final int LACK2 = 6;
    public static final int DAMAGE1 = 7;
//        public static final int DAMAGE2 = 8;

    public BtnListener(ViewHolder holder, int position, int type) {
      this.holder = holder;
      this.position = position;
      this.type = type;
    }

    @Override
    public void onClick(View v) {
      Animation begin = AnimationUtils.loadAnimation(context, R.anim.animation_actual_begin);
      Animation close = AnimationUtils.loadAnimation(context, R.anim.animation_actual_close);
      ActualDeliveryActivity.Goods goods = operationList.get(position);//操作数据
      ActualDeliveryActivity.Goods referGoods = list.get(position);//参考数据
      int newCount;
      if (type == ADD1) {
        holder.btn_cut.setClickable(true);
        String count = holder.et_quantity.getText().toString();
        if (StringTools.isEmp(count)) {
          count = "0";
        }
        newCount = Integer.parseInt(count);
        ++newCount;
        if (newCount > Integer.parseInt(referGoods.getCount())) {//不能大于运单创建时的数量
          Toast.makeText(context, R.string.prompt_is_limit, Toast.LENGTH_SHORT).show();
        } else {
          holder.et_quantity.setText(newCount + "");
          goods.setCount(newCount + "");
        }
      } else if (type == CUT1) {
        String count = holder.et_quantity.getText().toString();
        newCount = Integer.parseInt(count);
        --newCount;
        if (newCount < 0) {
          Toast.makeText(context, R.string.prompt_cannot_negative, Toast.LENGTH_LONG).show();
          holder.btn_cut.setClickable(false);
          newCount = 0;
        }
        if (newCount <= 0) {
          goods.setHasLack("false");
          goods.setHasDamage("false");
        }
        holder.et_quantity.setText(newCount + "");
        goods.setCount(newCount + "");
      } else if (type == LACK1) {
        if (!goods.getHasLack().equals("true")) {
          holder.btn_lack.setBackgroundColor(context.getResources().getColor(R.color.red_live_4));
          holder.btn_lack.setTextColor(Color.WHITE);
          goods.setHasLack("true");
          goods.setCount(holder.et_quantity.getText().toString());
        } else {
          holder.btn_lack.setBackgroundColor(context.getResources().getColor(R.color.second_bg_gray));
          holder.btn_lack.setTextColor(context.getResources().getColor(R.color.actual_text));
          goods.setHasLack("false");
          goods.setCount("0");
          holder.et_quantity.setText(0 + "");
        }
      } else if (type == DAMAGE1) {
        if (!goods.getHasDamage().equals("true")) {
          EventBus.getDefault().post(new Events.GoodsEvent(Events.TAkE_PHOTO, position, 1));
        } else {
          holder.btn_damage.setBackgroundColor(context.getResources().getColor(R.color.second_bg_gray));
          holder.btn_damage.setTextColor(context.getResources().getColor(R.color.actual_text));
          goods.setHasDamage("false");
        }
      }
      if (goods.getHasDamage().equals("false") && goods.getHasLack().equals("false") && goods.getCount().equals("0")) {
        if (holder.ll_damageType.getVisibility() == View.VISIBLE) {
          holder.ll_damageType.setAnimation(close);
          close.setAnimationListener(new Animation.AnimationListener() {//动画结束后才隐藏控件
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
              holder.ll_damageType.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
          });
        }
      } else {
        if (holder.ll_damageType.getVisibility() == View.GONE) {//没显示才显示
          holder.ll_damageType.startAnimation(begin);
          holder.ll_damageType.setVisibility(View.VISIBLE);
        }
      }
      operationList.remove(position);
      operationList.add(position, goods);
      moneyCount();
    }
  }

  private abstract class MyTextWatcher implements TextWatcher {
    private ViewHolder mHolder;

    public MyTextWatcher(ViewHolder holder) {
      this.mHolder = holder;
    }

    @Override
    public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
    }

    @Override
    public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
    }

    @Override
    public void afterTextChanged(Editable s) {
      afterTextChanged(s, mHolder);
    }

    public abstract void afterTextChanged(Editable s, ViewHolder holder);
  }

}
