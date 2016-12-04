package com.zzqs.app_kc.activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.igexin.sdk.PushManager;
import com.zzqs.app_kc.R;
import com.zzqs.app_kc.activities.OrderOperation.BatchOperationActivity;
import com.zzqs.app_kc.adapter.OrdersAdapter;
import com.zzqs.app_kc.app.ZZQSApplication;
import com.zzqs.app_kc.db.DaoManager;
import com.zzqs.app_kc.db.hibernate.dao.BaseDao;
import com.zzqs.app_kc.entity.Company;
import com.zzqs.app_kc.entity.DriverTrace;
import com.zzqs.app_kc.entity.Events;
import com.zzqs.app_kc.entity.Order;
import com.zzqs.app_kc.entity.OrderEvent;
import com.zzqs.app_kc.entity.User;
import com.zzqs.app_kc.fragment.ExecutingOrderFragment;
import com.zzqs.app_kc.fragment.UnPickupOrderFragment;
import com.zzqs.app_kc.fragment.WarehouseOrderFragment;
import com.zzqs.app_kc.net.RestAPI;
import com.zzqs.app_kc.service.HeartbeatService;
import com.zzqs.app_kc.service.LocationService;
import com.zzqs.app_kc.utils.ScreenUtil;
import com.zzqs.app_kc.utils.StringTools;
import com.zzqs.app_kc.widgets.DialogView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import de.greenrobot.event.EventBus;

/**
 * Created by lance on 15/11/21.
 */
public class MainActivity extends BaseActivity{
  private android.support.v4.app.FragmentManager mFragmentManager;
  private ViewPager mPager;//页卡内容
  private List<Fragment> listFragments; // Tab页面列表
  private ImageView cursor;// 动画图片
  private TextView t1, t2, t3, tvLeft, tvTitle;;// 页卡头标
  public int currIndex = 0;// 当前页卡编号
  public static UnPickupOrderFragment fragment1;
  public static ExecutingOrderFragment fragment2;
  public static WarehouseOrderFragment fragment3;
  private FragmentAdapter mFragmentAdapter;
  private int screenWidth;

  public TextView tv_unpick_tip, tv_transfer_tip;
  BaseDao<Order> orderBaseDao;


  private User user;
  private boolean isDialogShow = false;
  private static final int REFRESH = 98;
  private static final int DELETE = 99;
  private static final int REFRESH_UNPICK_ORDER = 100;
  private static final int REFRESH_WAREHOUSE_ORDER = 101;
  public static final int ORDER_CONFIRM = 102;
  public Handler handler = new Handler() {
    @Override
    public void handleMessage(Message msg) {
      super.handleMessage(msg);
      switch (msg.what) {
        case DELETE:
          DialogView.showChoiceDialog(MainActivity.this, DialogView.SINGLE_BTN, "提示", "运单" + msg.obj.toString() + "已被删除", null);
          break;
        case REFRESH_UNPICK_ORDER:
          fragment1.initData();
          break;
        case REFRESH_WAREHOUSE_ORDER:
          fragment3.initData();
          break;
        case REFRESH:
          fragment1.initData();
          fragment2.initData();
          break;
        case ORDER_CONFIRM:
          orderConfirm((Order) msg.obj);
          break;
        default:
          break;
      }
    }
  };

  private Timer timer;
  private TimerTask timerTask;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    PushManager.getInstance().initialize(getApplicationContext());
    setContentView(R.layout.act_main);
    orderBaseDao = DaoManager.getOrderDao(getApplicationContext());
    initView();
    InitImageView();
    InitTextView();
    InitViewPager();
    if (timerTask == null) {
      timerTask = new TimerTask() {
        @Override
        public void run() {
          if (!HeartbeatService.IS_LIVE) {
            startService(new Intent(getApplicationContext(), HeartbeatService.class));
          }
        }
      };
    }
    if (timer == null) {
      timer = new Timer();
      timer.schedule(timerTask, 0, 10 * 1000);
    }
  }

  @Override
  public void onResume() {
    super.onResume();
    user = ZZQSApplication.getInstance().getUser();
    initData();
    showTip();
  }

  private void initView() {
    tvLeft = (TextView) findViewById(R.id.head_back);
    tvLeft.setText("");
    tvLeft.setOnClickListener(new com.zzqs.app_kc.z_kc.listener.MyOnClickListener() {
      @Override
      public void OnceOnClick(View view) {
        finish();
      }
    });
    tvTitle = (TextView) findViewById(R.id.head_title);
    tvTitle.setText(R.string.view_tv_my_orders);
        downloadNewInformation();
    tv_unpick_tip = (TextView) findViewById(R.id.tv_unpick_tip);//新待提货提示
    tv_transfer_tip = (TextView) findViewById(R.id.tv_transfer_tip);//新仓储提示
  }


  private void initData() {
  }

  /**
   * 初始化头标
   */
  private void InitTextView() {
    t1 = (TextView) findViewById(R.id.text1);
    t2 = (TextView) findViewById(R.id.text2);
    t3 = (TextView) findViewById(R.id.text3);
    t1.setOnClickListener(new MyOnClickListener(0));
    t2.setOnClickListener(new MyOnClickListener(1));
    t3.setOnClickListener(new MyOnClickListener(2));
  }

  /**
   * 初始化ViewPager
   */
  private void InitViewPager() {
    mFragmentManager = getSupportFragmentManager();
    mPager = (ViewPager) findViewById(R.id.vPager);
    mPager.setOffscreenPageLimit(2);
    listFragments = new ArrayList<Fragment>();
    fragment1 = new UnPickupOrderFragment();
    fragment2 = new ExecutingOrderFragment();
    fragment3 = new WarehouseOrderFragment();
    listFragments.add(fragment1);
    listFragments.add(fragment2);
    listFragments.add(fragment3);
    mFragmentAdapter = new FragmentAdapter(mFragmentManager, listFragments);
    mPager.setAdapter(mFragmentAdapter);
    mPager.setOnPageChangeListener(new MyOnPageChangeListener());
    mPager.setCurrentItem(currIndex);
  }

  /**
   * 初始化动画
   */
  private void InitImageView() {
    cursor = (ImageView) findViewById(R.id.cursor);
    screenWidth = ScreenUtil.getScreenWidth(getApplicationContext());// 获取分辨率宽度
    RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) cursor.getLayoutParams();
    lp.width = screenWidth / 3;
    cursor.setLayoutParams(lp);
  }

  /**
   * 现实红点标签提示
   */
  private void showTip() {
    List<Order> orders = orderBaseDao.find(null, "is_new=?", new String[]{Order.NEW + ""}, null, null, null, null);
    if (orders.size() == 0) {
      tv_unpick_tip.setVisibility(View.INVISIBLE);
      tv_transfer_tip.setVisibility(View.INVISIBLE);
      return;
    }
    for (Order order : orders) {
      if (order.getOrderType().equals(Order.DRIVER_ORDER)) {
        tv_unpick_tip.setVisibility(View.VISIBLE);
        break;
      }
    }
    for (Order order : orders) {
      if (order.getOrderType().equals(Order.WAREHOUSE_ORDER)) {
        tv_transfer_tip.setVisibility(View.VISIBLE);
        break;
      }
    }
  }

  private void toBatch(List<Order> orders) {
    Intent intent = new Intent(getApplicationContext(), BatchOperationActivity.class);
    ArrayList<String> orderIdList = new ArrayList<>();
    for (Order order : orders) {
      orderIdList.add(order.getOrderId());
    }
    intent.putStringArrayListExtra(Order.ORDER_IDS, orderIdList);
    startActivity(intent);
  }


  /**
   * ViewPager适配器
   */
  public class FragmentAdapter extends FragmentPagerAdapter {

    List<Fragment> fragmentList = new ArrayList<Fragment>();

    public FragmentAdapter(android.support.v4.app.FragmentManager fm, List<Fragment> fragmentList) {
      super(fm);
      this.fragmentList = fragmentList;
    }

    @Override
    public Fragment getItem(int position) {
      return fragmentList.get(position);
    }

    @Override
    public int getCount() {
      return fragmentList.size();
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
      return super.instantiateItem(container, position);
    }
  }

  /**
   * 头标点击监听
   */
  public class MyOnClickListener implements View.OnClickListener {
    private int index = 0;

    public MyOnClickListener(int i) {
      index = i;
    }

    @Override
    public void onClick(View v) {
      mPager.setCurrentItem(index);
    }
  }

  private void resetTextView() {
    t1.setTextColor(getResources().getColor(R.color.dark_blue));
    t2.setTextColor(getResources().getColor(R.color.dark_blue));
    t3.setTextColor(getResources().getColor(R.color.dark_blue));
  }

  /**
   * 页卡切换监听
   */
  public class MyOnPageChangeListener implements ViewPager.OnPageChangeListener {


    @Override
    public void onPageSelected(int position) {
      resetTextView();
      switch (position) {
        case 0:
          if (fragment1.adapter != null) {
            fragment1.adapter.notifyDataSetChanged();
          }
          t1.setTextColor(Color.WHITE);
          break;
        case 1:
          if (fragment2.adapter != null) {
            fragment2.adapter.notifyDataSetChanged();
          }
          t2.setTextColor(Color.WHITE);
          break;
        case 2:
          if (fragment3.adapter != null) {
            fragment3.adapter.notifyDataSetChanged();
          }
          t3.setTextColor(Color.WHITE);
          break;
      }
      currIndex = position;
    }

    @Override
    public void onPageScrolled(int position, float offset, int offsetPixels) {
      RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) cursor
          .getLayoutParams();

      /**
       * 利用currIndex(当前所在页面)和position(下一个页面)以及offset来
       * 设置mTabLineIv的左边距 滑动场景：
       * 记3个页面,
       * 从左到右分别为0,1,2
       * 0->1; 1->2; 2->1; 1->0
       */

      if (currIndex == 0 && position == 0)// 0->1
      {
        lp.leftMargin = (int) (offset * (screenWidth * 1.0 / 3) + currIndex * (screenWidth / 3));
      } else if (currIndex == 1 && position == 0) // 1->0
      {
        lp.leftMargin = (int) (-(1 - offset) * (screenWidth * 1.0 / 3) + currIndex * (screenWidth / 3));

      } else if (currIndex == 1 && position == 1) // 1->2
      {
        lp.leftMargin = (int) (offset * (screenWidth * 1.0 / 3) + currIndex * (screenWidth / 3));
      } else if (currIndex == 2 && position == 1) // 2->1
      {
        lp.leftMargin = (int) (-(1 - offset) * (screenWidth * 1.0 / 3) + currIndex * (screenWidth / 3));
      }
      cursor.setLayoutParams(lp);
    }

    @Override
    public void onPageScrollStateChanged(int arg0) {
    }
  }

  public static void selectWayOrder(Order order, boolean select) {
    ArrayList<Order> seekList = null;
    OrdersAdapter adapter = null;
    if (order.getStatus().equals(Order.UN_PICKUP_ENTRANCE) || order.getStatus().equals(order.UN_PICKUP)) {
      seekList = fragment1.seekList;
      adapter = fragment1.adapter;
    } else if (order.getStatus().equals(Order.UN_DELIVERY_ENTRANCE) || order.getStatus().equals(Order.UN_DELIVERY)) {
      seekList = fragment2.seekList;
      adapter = fragment2.adapter;
    } else if (order.getStatus().equals(Order.WAREHOUSE_ORDER)) {
      seekList = fragment3.seekList;
      adapter = fragment3.adapter;
    }
    adapter.notifyDataSetChanged();
  }

  private void downloadNewInformation() {
    final BaseDao<Order> orderDao = DaoManager.getOrderDao(getApplicationContext());
    final BaseDao<Company> companyDao = DaoManager.getCompanyDao(getApplicationContext());
    String[] status = new String[]{Order.UN_PICKUP_ENTRANCE, Order.UN_PICKUP, Order.UN_DELIVERY_ENTRANCE, Order.UN_DELIVERY};
    RestAPI.getInstance(getApplicationContext()).getOrders(status, Order.DRIVER_ORDER, new RestAPI.RestResponse() {
      @Override
      public void onSuccess(Object object) {
        final List<Order> newList = (ArrayList<Order>) object;
        if (newList.size() > 0) {
          new Thread() {
            @Override
            public void run() {
              super.run();
              List<Order> newOrders = new ArrayList<Order>();
              Message message = handler.obtainMessage();
              StringBuffer stringBuffer = new StringBuffer();
              ArrayList<Order> updateList = new ArrayList<>();
              ArrayList<Order> insertList = new ArrayList<>();
              for (Order order : newList) {
                List<Order> list = orderDao.find(null, "order_id=?", new String[]{order.getOrderId() + ""}
                    , null, null, null, null);
                if (list == null || list.size() == 0) {
                  if (!order.getStatus().equals(Order.STATUS_INVALID)) {
                    if (order.getStatus().equals(Order.UN_PICKUP_ENTRANCE) || order.getStatus().equals(Order.UN_PICKUP)) {
                      order.setIsNew(Order.NEW);
                    }
                    insertList.add(order);
                    newOrders.add(order);
                  }
                } else {
                  order.set_id(list.get(0).get_id());
                  order.setIsNew(list.get(0).getIsNew());
                  updateList.add(order);
                  if (!list.get(0).getStatus().equals(Order.STATUS_INVALID) && order.getStatus().equals(Order.STATUS_INVALID)) {
                    EventBus.getDefault().post(new Events.OrderEvent(order, Events.UPDATE_ORDER_IN_PICKUP_ORDER_FRAGMENT));
                    message.what = DELETE;
                    stringBuffer.append(order.getSerialNo() + ",");
                  }
                }
              }
              orderDao.inserts(insertList);
              orderDao.updates(updateList);
              if (stringBuffer.length() > 0) {
                message.obj = stringBuffer.toString().substring(0, stringBuffer.length() - 2);
                handler.sendMessage(message);
              }
              handler.sendEmptyMessage(REFRESH);
            }
          }.start();
        }
      }

      @Override
      public void onFailure(Object object) {
        if (object.toString().equals("disconnected") && !isDialogShow) {
          isDialogShow = true;
          DialogView.showChoiceDialog(ZZQSApplication.getInstance().getCurrentContext(), DialogView.SINGLE_BTN, getString(R.string.prompt_dl_other_equipment_login_title), getString(R.string.prompt_dl_other_equipment_login_msg), new Handler() {
            @Override
            public void handleMessage(Message msg) {
              ZZQSApplication.getInstance().clearUser(MainActivity.this);
              ZZQSApplication.getInstance().cleanAllActivity();
              startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            }
          });
        } else {
          Toast.makeText(getApplicationContext(), object.toString(), Toast.LENGTH_SHORT).show();
        }
      }
    });
    RestAPI.getInstance(this).getOrders(new String[]{Order.UN_DELIVERY}, Order.WAREHOUSE_ORDER, new RestAPI.RestResponse() {
      @Override
      public void onSuccess(Object object) {
        final List<Order> newList = (ArrayList<Order>) object;
        if (newList.size() > 0) {
          new Thread() {
            @Override
            public void run() {
              super.run();
              List<Order> newOrders = new ArrayList<Order>();
              boolean haveNewOrder = false;
              ArrayList<Order> updateList = new ArrayList<>();
              ArrayList<Order> insertList = new ArrayList<>();
              for (Order order : newList) {
                List<Order> list = orderDao.find(null, "order_id=?", new String[]{order.getOrderId() + ""}
                    , null, null, null, null);
                if (list == null || list.size() == 0) {
                  insertList.add(order);
                  newOrders.add(order);
                  haveNewOrder = true;
                } else {
                  order.set_id(list.get(0).get_id());
                  updateList.add(order);
                  if (!list.get(0).getStatus().equals(Order.STATUS_INVALID) && order.getStatus().equals(Order.STATUS_INVALID)) {
                    EventBus.getDefault().post(new Events.OrderEvent(order, Events.UPDATE_ORDER_IN_WAREHOUSE_ORDER_FRAGMENT));
                  }
                }
              }
              orderDao.inserts(insertList);
              orderDao.updates(updateList);
              if (haveNewOrder) {
                handler.sendEmptyMessage(REFRESH_WAREHOUSE_ORDER);
              }
            }
          }.start();
        }
      }

      @Override
      public void onFailure(Object object) {
        if (object.toString().equals("disconnected") && !isDialogShow) {
          isDialogShow = true;
          DialogView.showChoiceDialog(ZZQSApplication.getInstance().getCurrentContext(), DialogView.SINGLE_BTN, getString(R.string.prompt_dl_other_equipment_login_title), getString(R.string.prompt_dl_other_equipment_login_msg), new Handler() {
            @Override
            public void handleMessage(Message msg) {
              ZZQSApplication.getInstance().clearUser(MainActivity.this);
              ZZQSApplication.getInstance().cleanAllActivity();
              startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            }
          });
        } else {
          Toast.makeText(MainActivity.this, object.toString(), Toast.LENGTH_SHORT).show();
        }
      }
    });
    RestAPI.getInstance(getApplicationContext()).getCompanies(new RestAPI.RestResponse() {
      @Override
      public void onSuccess(Object object) {
        final List<Company> list = (List<Company>) object;
        if (list.size() > 0) {
          ArrayList<Company> companies = new ArrayList<Company>();
          for (Company company : list) {
            company.setUsername(user.getUsername());
            List<Company> localList = companyDao.find(null, "company_id=?", new String[]{company.getCompany_id() + ""}, null, null, null, null);
            if (null == localList || localList.size() == 0) {
              companies.add(company);
            }
            companyDao.inserts(companies);
          }
        }
      }

      @Override
      public void onFailure(Object object) {
        if (object.toString().equals("disconnected") && !isDialogShow) {
          isDialogShow = true;
          DialogView.showChoiceDialog(ZZQSApplication.getInstance().getCurrentContext(), DialogView.SINGLE_BTN, getString(R.string.prompt_dl_other_equipment_login_title), getString(R.string.prompt_dl_other_equipment_login_msg), new Handler() {
            @Override
            public void handleMessage(Message msg) {
              ZZQSApplication.getInstance().clearUser(MainActivity.this);
              ZZQSApplication.getInstance().cleanAllActivity();
              startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            }
          });
        } else {
          Toast.makeText(MainActivity.this, object.toString(), Toast.LENGTH_SHORT).show();
        }
      }
    });
  }

  private void showHints() {
    new AlertDialog.Builder(this)
        .setTitle(getString(R.string.prompt_dl_title_1))
        .setMessage(getString(R.string.prompt_dl_login_out))
        .setPositiveButton(getString(R.string.view_bt_ok), new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
            dialog.dismiss();
            stopService(new Intent(MainActivity.this, LocationService.class));
            ZZQSApplication.getInstance().clearUser(MainActivity.this);
            ZZQSApplication.getInstance().cleanAllActivity();
            Intent intent = new Intent();
            intent.setClass(getApplicationContext(), LaunchActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);  //注意本行的FLAG设置
            startActivity(intent);

          }
        })
        .setNegativeButton(getString(R.string.view_tv_cancel), new DialogInterface.OnClickListener() {

          @Override
          public void onClick(DialogInterface dialog, int which) {
            dialog.dismiss();

          }
        }).setCancelable(true).show();
  }

  @Override
  protected void onSaveInstanceState(Bundle outState) {
//        super.onSaveInstanceState(outState);
  }


  @Override
  protected void onRestoreInstanceState(Bundle savedInstanceState) {
    super.onRestoreInstanceState(savedInstanceState);
  }

  public void orderConfirm(final Order order) {
    final ProgressDialog pd = new ProgressDialog(this);
    pd.show();
    final OrderEvent orderEvent = new OrderEvent();
    orderEvent.setMold(OrderEvent.MOLD_CONFIRM);
    orderEvent.setOrderId(order.getOrderId());
    orderEvent.setStatus(OrderEvent.STATUS_NEW);
    if (LocationService.isStart && LocationService.lastTrace != null) {
      DriverTrace lastTrace = LocationService.lastTrace;
      orderEvent.setLatitude(lastTrace.getLatitude());
      orderEvent.setLongitude(lastTrace.getLongitude());
      if (!StringTools.isEmp(lastTrace.getAddress())) {
        orderEvent.setAddress(lastTrace.getAddress());
      }
      if (!StringTools.isEmp(lastTrace.getTime()) && !lastTrace.getTime().equals("1970-01-01 00:00:00")) {
        orderEvent.setCreateTime(lastTrace.getTime());
      } else {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        orderEvent.setCreateTime(sdf.format(new Date()));
      }
    }
    final BaseDao<Order> orderDao = DaoManager.getOrderDao(getApplicationContext());
    final BaseDao<OrderEvent> orderEventDao = DaoManager.getOrderEventDao(getApplicationContext());
    RestAPI.getInstance(getApplicationContext()).uploadEvent(order.getUpdateTime(), orderEvent, null, null, null, null, new RestAPI.RestResponse() {
      @Override
      public void onSuccess(Object object) {
        order.setConfirmStatus(Order.CONFIRMED);
        order.setIsNew(Order.EXIST);
        orderEvent.setStatus(OrderEvent.STATUS_COMMIT);
        orderDao.update(order);
        orderEventDao.insert(orderEvent);
        handler.sendEmptyMessage(order.getOrderType().equals(Order.DRIVER_ORDER) ? REFRESH_UNPICK_ORDER : REFRESH_WAREHOUSE_ORDER);
        pd.dismiss();
        Toast.makeText(MainActivity.this, getString(R.string.prompt_order_confirm_success), Toast.LENGTH_SHORT).show();
      }

      @Override
      public void onFailure(Object object) {
        if (object.toString().equals(Order.ORDER_NOT_EXIST)) {
          order.setStatus(Order.STATUS_INVALID);
          order.setConfirmStatus(Order.CONFIRMED);
          order.setIsNew(Order.EXIST);
          orderDao.update(order);
          handler.sendEmptyMessage(order.getOrderType().equals(Order.DRIVER_ORDER) ? REFRESH_UNPICK_ORDER : REFRESH_WAREHOUSE_ORDER);
          pd.dismiss();
          Toast.makeText(MainActivity.this, getString(R.string.prompt_order_cancel), Toast.LENGTH_SHORT).show();
        } else if (object.toString().equals(Order.ORDER_STATUS_HAS_CHANGED)) {
          RestAPI.getInstance(getApplicationContext()).getOrder(order.getOrderId(), new RestAPI.RestResponse() {
            @Override
            public void onSuccess(Object object) {
              if (object instanceof Order) {
                Order newOrder = (Order) object;
                newOrder.set_id(order.get_id());
                newOrder.setIsNew(Order.EXIST);
                newOrder.setConfirmStatus(Order.CONFIRMED);
                orderDao.update(newOrder);
                handler.sendEmptyMessage(order.getOrderType().equals(Order.DRIVER_ORDER) ? REFRESH_UNPICK_ORDER : REFRESH_WAREHOUSE_ORDER);
                pd.dismiss();
                Toast.makeText(MainActivity.this, getString(R.string.prompt_order_change), Toast.LENGTH_SHORT).show();
              }
            }

            @Override
            public void onFailure(Object object) {
              if (object.toString().equals(Order.ORDER_NOT_EXIST)) {
                order.setStatus(Order.STATUS_INVALID);
                order.setIsNew(Order.EXIST);
                orderDao.update(order);
                handler.sendEmptyMessage(order.getOrderType().equals(Order.DRIVER_ORDER) ? REFRESH_UNPICK_ORDER : REFRESH_WAREHOUSE_ORDER);
                pd.dismiss();
                Toast.makeText(MainActivity.this, getString(R.string.prompt_order_cancel), Toast.LENGTH_SHORT).show();
              } else if (object.toString().equals("disconnected")) {
                DialogView.showChoiceDialog(ZZQSApplication.getInstance().getCurrentContext(), DialogView.SINGLE_BTN, getString(R.string.prompt_dl_other_equipment_login_title), getString(R.string.prompt_dl_other_equipment_login_msg), new Handler() {
                  @Override
                  public void handleMessage(Message msg) {
                    ZZQSApplication.getInstance().clearUser(MainActivity.this);
                    ZZQSApplication.getInstance().cleanAllActivity();
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                  }
                });
              }
            }
          });
        } else if (object.toString().equals("disconnected")) {
          DialogView.showChoiceDialog(ZZQSApplication.getInstance().getCurrentContext(), DialogView.SINGLE_BTN, getString(R.string.prompt_dl_other_equipment_login_title), getString(R.string.prompt_dl_other_equipment_login_msg), new Handler() {
            @Override
            public void handleMessage(Message msg) {
              ZZQSApplication.getInstance().clearUser(MainActivity.this);
              ZZQSApplication.getInstance().cleanAllActivity();
              startActivity(new Intent(MainActivity.this, LoginActivity.class));
            }
          });
        } else {
          Toast.makeText(MainActivity.this, object.toString(), Toast.LENGTH_SHORT).show();
        }
      }
    });
  }
}
