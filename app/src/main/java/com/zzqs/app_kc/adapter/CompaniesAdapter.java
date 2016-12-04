package com.zzqs.app_kc.adapter;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.zzqs.app_kc.R;
import com.zzqs.app_kc.db.hibernate.dao.BaseDao;
import com.zzqs.app_kc.entity.Company;
import com.zzqs.app_kc.net.RestAPI;
import com.zzqs.app_kc.widgets.DialogView;

import java.util.ArrayList;

/**
 * Created by lance on 15/3/23.
 */
public class CompaniesAdapter extends BaseAdapter {
    private ArrayList<Company> companies;
    private Context context;
    private BaseDao<Company> companyDao;

    public CompaniesAdapter(ArrayList<Company> companies, BaseDao companyDao, Context context) {
        this.companies = companies;
        this.companyDao = companyDao;
        this.context = context;
    }

    @Override
    public int getCount() {
        return companies.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        final Company company = companies.get(i);
        final ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.item_company, null);
            holder.company_name = (TextView) convertView.findViewById(R.id.company_name);
            holder.accept = (Button) convertView.findViewById(R.id.accept);
            holder.refused = (Button) convertView.findViewById(R.id.refused);
            holder.is_accept = (TextView) convertView.findViewById(R.id.is_accept);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        if (null != company.getCompany_name() + "") {
            holder.company_name.setText(company.getCompany_name());
        }
        if (company.getStatus() == Company.UN_ACCEPT) {
            holder.is_accept.setVisibility(View.GONE);
            holder.accept.setVisibility(View.VISIBLE);
            holder.refused.setVisibility(View.VISIBLE);
            holder.accept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    DialogView.showChoiceDialog(context, DialogView.NORMAL, context.getString(R.string.prompt_dl_title_1),context.getString(R.string.prompt_dl_confirm_accept), new Handler() {
                        @Override
                        public void handleMessage(Message msg) {
                            super.handleMessage(msg);
                            if (msg.what == DialogView.ACCEPT) {
                                RestAPI.getInstance(context).accept(company.getCompany_id(), new RestAPI.RestResponse() {
                                    @Override
                                    public void onSuccess(Object object) {
                                        company.setStatus(Company.ACCEPT);
                                        holder.is_accept.setVisibility(View.VISIBLE);
                                        holder.accept.setVisibility(View.GONE);
                                        holder.refused.setVisibility(View.GONE);
                                        companyDao.execSql("update company set status=? where company_id=?", new String[]{Company.ACCEPT + "", company.getCompany_id()});
                                        Toast.makeText(context, R.string.prompt_accept_company_success, Toast.LENGTH_SHORT).show();
                                    }

                                    @Override
                                    public void onFailure(Object object) {
                                        companies.remove(company);
                                        companyDao.execSql("delete from company where company_id=?", new String[]{company.getCompany_id()});
                                        notifyDataSetChanged();
                                        Toast.makeText(context, object.toString(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }
                    });
                }
            });
            holder.refused.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    DialogView.showChoiceDialog(context, DialogView.NORMAL, context.getString(R.string.prompt_dl_title_1),context.getString(R.string.prompt_dl_confirm_refused), new Handler() {
                        @Override
                        public void handleMessage(Message msg) {
                            super.handleMessage(msg);
                            if (msg.what == DialogView.ACCEPT) {
                                RestAPI.getInstance(context).refused(company.getCompany_id(), new RestAPI.RestResponse() {
                                    @Override
                                    public void onSuccess(Object object) {
                                        companyDao.delete(company.get_id());
                                        companies.remove(company);
                                        notifyDataSetChanged();
                                        Toast.makeText(context, R.string.prompt_refused_company_success, Toast.LENGTH_SHORT).show();
                                    }

                                    @Override
                                    public void onFailure(Object object) {
                                        companyDao.delete(company.get_id());
                                        companies.remove(company);
                                        notifyDataSetChanged();
                                        Toast.makeText(context, object.toString(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }
                    });
                }
            });
        } else {
            holder.is_accept.setVisibility(View.VISIBLE);
            holder.accept.setVisibility(View.GONE);
            holder.refused.setVisibility(View.GONE);
        }
        return convertView;
    }

    private class ViewHolder {
        Button accept, refused;
        TextView company_name, is_accept;
    }
}
