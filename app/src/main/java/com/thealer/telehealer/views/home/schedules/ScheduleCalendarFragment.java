package com.thealer.telehealer.views.home.schedules;

import android.animation.Animator;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.RectF;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alamkanak.weekview.DateTimeInterpreter;
import com.alamkanak.weekview.EventClickListener;
import com.alamkanak.weekview.MonthLoader;
import com.alamkanak.weekview.OnVerticalScrollListener;
import com.alamkanak.weekview.ScrollListener;
import com.alamkanak.weekview.WeekView;
import com.alamkanak.weekview.WeekViewDisplayable;
import com.alamkanak.weekview.WeekViewEvent;
import com.google.gson.Gson;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.CalendarMode;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.prolificinteractive.materialcalendarview.OnMonthChangedListener;
import com.prolificinteractive.materialcalendarview.spans.DotSpan;
import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.baseapimodel.BaseApiResponseModel;
import com.thealer.telehealer.apilayer.baseapimodel.BaseApiViewModel;
import com.thealer.telehealer.apilayer.baseapimodel.ErrorModel;
import com.thealer.telehealer.apilayer.models.associationlist.AssociationApiViewModel;
import com.thealer.telehealer.apilayer.models.commonResponseModel.CommonUserApiResponseModel;
import com.thealer.telehealer.apilayer.models.schedules.SchedulesApiResponseModel;
import com.thealer.telehealer.apilayer.models.schedules.SchedulesApiViewModel;
import com.thealer.telehealer.common.ArgumentKeys;
import com.thealer.telehealer.common.Constants;
import com.thealer.telehealer.common.CustomButton;
import com.thealer.telehealer.common.PreferenceConstants;
import com.thealer.telehealer.common.UserType;
import com.thealer.telehealer.common.Utils;
import com.thealer.telehealer.common.emptyState.EmptyStateUtil;
import com.thealer.telehealer.common.emptyState.EmptyViewConstants;
import com.thealer.telehealer.views.base.BaseFragment;
import com.thealer.telehealer.views.common.AttachObserverInterface;
import com.thealer.telehealer.views.common.OverlayViewConstants;
import com.thealer.telehealer.views.common.ShowSubFragmentInterface;

import org.threeten.bp.LocalDate;
import org.threeten.bp.format.DateTimeFormatter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import me.toptas.fancyshowcase.listener.DismissListener;

import static android.content.Context.ALARM_SERVICE;
import static com.thealer.telehealer.TeleHealerApplication.appPreference;

/**
 * Created by Aswin on 02,January,2019
 */
public class ScheduleCalendarFragment extends BaseFragment implements EventClickListener, MonthLoader.MonthChangeListener, ScrollListener {

    private MaterialCalendarView calendarview;
    private WeekView weekView;

    private SchedulesApiViewModel schedulesApiViewModel;
    private AttachObserverInterface attachObserverInterface;
    private SchedulesApiResponseModel schedulesApiResponseModel;
    private AssociationApiViewModel associationApiViewModel;
    private ShowSubFragmentInterface showSubFragmentInterface;

    private StringBuilder doctorGuidList = new StringBuilder();
    private ArrayList<SchedulesApiResponseModel.ResultBean> responseModelArrayList;
    private HashSet<CalendarDay> calendarDayHashSet;
    private List<WeekViewDisplayable> weekViewEventList = new ArrayList<>();
    private FloatingActionButton addFab;
    private ConstraintLayout recyclerEmptyStateView;
    private ImageView emptyIv;
    private TextView emptyTitleTv;
    private TextView emptyMessageTv;
    private Calendar currentDay;
    private LinearLayout parent;
    private boolean isWeekScrollEnabled = false;
    private Set<String> notificationCreatedId = new HashSet<>();
    private LocalNotificationReceiver localNotificationReceiver = null;
    private CustomButton emptyActionBtn;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        attachObserverInterface = (AttachObserverInterface) getActivity();
        showSubFragmentInterface = (ShowSubFragmentInterface) getActivity();

        associationApiViewModel = ViewModelProviders.of(this).get(AssociationApiViewModel.class);
        schedulesApiViewModel = ViewModelProviders.of(this).get(SchedulesApiViewModel.class);

        attachObserverInterface.attachObserver(schedulesApiViewModel);
        attachObserverInterface.attachObserver(associationApiViewModel);

        schedulesApiViewModel.baseApiArrayListMutableLiveData.observe(this, new Observer<ArrayList<BaseApiResponseModel>>() {
            @Override
            public void onChanged(@Nullable ArrayList<BaseApiResponseModel> baseApiResponseModels) {
                if (baseApiResponseModels != null) {
                    setEmptyState();
                    responseModelArrayList = (ArrayList<SchedulesApiResponseModel.ResultBean>) (Object) baseApiResponseModels;
                    updateCalendar();
                    if (responseModelArrayList.size() == 0 && !appPreference.getBoolean(PreferenceConstants.IS_OVERLAY_ADD_SCHEDULE)) {
                        appPreference.setBoolean(PreferenceConstants.IS_OVERLAY_ADD_SCHEDULE, true);
                        emptyMessageTv.setVisibility(View.GONE);
                        Utils.showOverlay(getActivity(), addFab, OverlayViewConstants.OVERLAY_NO_APPOINTMENT, new DismissListener() {
                            @Override
                            public void onDismiss(@org.jetbrains.annotations.Nullable String s) {
                                emptyMessageTv.setVisibility(View.VISIBLE);
                            }

                            @Override
                            public void onSkipped(@org.jetbrains.annotations.Nullable String s) {

                            }
                        });

                    }
                }
            }
        });

        schedulesApiViewModel.getErrorModelLiveData().observe(this, new Observer<ErrorModel>() {
            @Override
            public void onChanged(@Nullable ErrorModel errorModel) {
                if (errorModel != null) {
                    switch (errorModel.getCode()) {
                        case BaseApiViewModel
                                .NETWORK_ERROR_CODE:
                            setNoNetworkEmptyState();
                            break;
                    }
                }
            }
        });

        associationApiViewModel.baseApiArrayListMutableLiveData.observe(this, new Observer<ArrayList<BaseApiResponseModel>>() {
            @Override
            public void onChanged(@Nullable ArrayList<BaseApiResponseModel> baseApiResponseModels) {
                if (baseApiResponseModels != null) {
                    ArrayList<CommonUserApiResponseModel> commonUserApiResponseModels = (ArrayList<CommonUserApiResponseModel>) (Object) baseApiResponseModels;

                    for (int i = 0; i < commonUserApiResponseModels.size(); i++) {
                        if (doctorGuidList.length() == 0) {
                            doctorGuidList = doctorGuidList.append(commonUserApiResponseModels.get(i).getUser_guid());
                        } else {
                            doctorGuidList = doctorGuidList.append(",").append(commonUserApiResponseModels.get(i).getUser_guid());
                        }
                    }

                    appPreference.setString(PreferenceConstants.ASSOCIATION_GUID_LIST, doctorGuidList.toString());

                    getAllSchedules();
                }
            }
        });

    }

    private void updateCalendar() {
        calendarDayHashSet = new HashSet<>();
        calendarDayHashSet.clear();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(Utils.UTCFormat);

        weekViewEventList.clear();

        createScheduledNotification();

        List<SchedulesApiResponseModel.ResultBean> schedulesList = new ArrayList<>();

        for (int i = 0; i < responseModelArrayList.size(); i++) {

            if (!Utils.isDateTimeExpired(responseModelArrayList.get(i).getStart())) {
                if (!notificationCreatedId.contains(String.valueOf(responseModelArrayList.get(i).getSchedule_id()))) {
                    notificationCreatedId.add(String.valueOf(responseModelArrayList.get(i).getSchedule_id()));
                    appPreference.setStringSet(PreferenceConstants.NOTIFICATIONS_IDS, notificationCreatedId);
                    addLocalNotification(responseModelArrayList.get(i));
                }
            } else {
                if (notificationCreatedId.contains(String.valueOf(responseModelArrayList.get(i).getSchedule_id()))) {
                    notificationCreatedId.remove(String.valueOf(responseModelArrayList.get(i).getSchedule_id()));
                    appPreference.setStringSet(PreferenceConstants.NOTIFICATIONS_IDS, notificationCreatedId);
                }
            }

            if (!Utils.isDateTimeExpired(responseModelArrayList.get(i).getEnd())) {
                schedulesList.add(responseModelArrayList.get(i));
            }

            CalendarDay calendarDay = CalendarDay.from(LocalDate.parse(responseModelArrayList.get(i).getStart(), dateTimeFormatter));
            calendarDayHashSet.add(calendarDay);

            String eventTitle;

            CommonUserApiResponseModel doctorModel = null, patientModel = null;

            if (responseModelArrayList.get(i).getScheduled_by_user().getRole().equals(Constants.ROLE_PATIENT)) {
                patientModel = responseModelArrayList.get(i).getScheduled_by_user();
            } else {
                doctorModel = responseModelArrayList.get(i).getScheduled_by_user();
            }
            if (responseModelArrayList.get(i).getScheduled_with_user().getRole().equals(Constants.ROLE_PATIENT)) {
                patientModel = responseModelArrayList.get(i).getScheduled_with_user();
            } else {
                doctorModel = responseModelArrayList.get(i).getScheduled_with_user();
            }


            if (UserType.isUserAssistant()) {
                eventTitle = doctorModel.getUserDisplay_name() + "-" + patientModel.getUserDisplay_name();
            } else if (UserType.isUserDoctor()) {
                eventTitle = patientModel.getUserDisplay_name();
            } else {
                eventTitle = doctorModel.getUserDisplay_name();
            }

            WeekViewEvent weekViewEvent = new WeekViewEvent(responseModelArrayList.get(i).getSchedule_id(),
                    eventTitle,
                    Utils.getCalendar(responseModelArrayList.get(i).getStart()),
                    Utils.getCalendar(responseModelArrayList.get(i).getEnd()),
                    null,
                    getActivity().getColor(R.color.calendar_event_color),
                    false,
                    responseModelArrayList.get(i));

            weekViewEventList.add(weekViewEvent);

        }

        String upcomingList = new Gson().toJson(schedulesList);

        appPreference.setString(PreferenceConstants.UPCOMING_SCHEDULES, upcomingList);

        checkOnGoingCall();

        weekView.notifyDataSetChanged();

        calendarview.setCurrentDate(CalendarDay.today());
        updateEvents(CalendarDay.today());

        calendarview.addDecorator(new DayViewDecorator() {
            @Override
            public boolean shouldDecorate(CalendarDay calendarDay) {
                return calendarDayHashSet.contains(calendarDay);
            }

            @Override
            public void decorate(DayViewFacade dayViewFacade) {
                dayViewFacade.addSpan(new DotSpan(5f));
            }
        });

    }

    private void createScheduledNotification() {
        if (localNotificationReceiver == null) {
            String action = getString(R.string.notification_intent_action);

            IntentFilter intentFilter = new IntentFilter(action);
            intentFilter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);

            localNotificationReceiver = new LocalNotificationReceiver();
            if (getActivity() != null) {
                getActivity().registerReceiver(localNotificationReceiver, intentFilter);
            }
        }
    }

    private void addLocalNotification(SchedulesApiResponseModel.ResultBean resultBean) {

        String action = getString(R.string.notification_intent_action);

        Intent notificationIntent = new Intent(getActivity(), LocalNotificationReceiver.class);
        notificationIntent.setAction(action);
        notificationIntent.addCategory(Intent.CATEGORY_DEFAULT);
        notificationIntent.putExtra(ArgumentKeys.SCHEDULE_DETAIL, new Gson().toJson(resultBean));

        PendingIntent pendingIntent = PendingIntent.getBroadcast(getActivity(), resultBean.getSchedule_id(), notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Date date = Utils.getDateFromString(resultBean.getStart());
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MINUTE, -10);

        if (!Utils.isDateTimeExpired(calendar.getTime())) {
            AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(ALARM_SERVICE);
            alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        }

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_schedule_calendar_view, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        calendarview = (MaterialCalendarView) view.findViewById(R.id.calendarview);
        weekView = (WeekView) view.findViewById(R.id.weekView);
        addFab = (FloatingActionButton) view.findViewById(R.id.add_fab);
        recyclerEmptyStateView = (ConstraintLayout) view.findViewById(R.id.recycler_empty_state_view);
        emptyIv = (ImageView) view.findViewById(R.id.empty_iv);
        emptyTitleTv = (TextView) view.findViewById(R.id.empty_title_tv);
        emptyMessageTv = (TextView) view.findViewById(R.id.empty_message_tv);
        parent = (LinearLayout) view.findViewById(R.id.parent);
        emptyActionBtn = (CustomButton) view.findViewById(R.id.empty_action_btn);

        notificationCreatedId = appPreference.getStringSet(PreferenceConstants.NOTIFICATIONS_IDS);

        setEmptyState();

        addFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addFab.setClickable(false);
                startActivity(new Intent(getActivity(), CreateNewScheduleActivity.class));
            }
        });

        setUpWeekView();

        calendarview.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView materialCalendarView, @NonNull CalendarDay calendarDay, boolean b) {

                if (calendarview.getCalendarMode() == CalendarMode.MONTHS) {
                    setCalendarMode(CalendarMode.WEEKS);
                    isWeekScrollEnabled = true;
                }
                calendarview.setSelectedDate(calendarDay);
                calendarview.setCurrentDate(calendarDay);
                calendarview.setDateSelected(calendarDay, true);
                updateEvents(calendarDay);
            }
        });

        setCalendarMode(CalendarMode.MONTHS);

        calendarview.setDateSelected(CalendarDay.today(), true);
        calendarview.setDynamicHeightEnabled(true);
        calendarview.setAllowClickDaysOutsideCurrentMonth(true);

        calendarview.setOnMonthChangedListener(new OnMonthChangedListener() {
            @Override
            public void onMonthChanged(MaterialCalendarView materialCalendarView, CalendarDay calendarDay) {
                if (isWeekScrollEnabled && calendarview.getCalendarMode() == CalendarMode.WEEKS && calendarview.getSelectedDate() != null) {

                    LocalDate localDate = LocalDate.of(calendarview.getSelectedDate().getYear(), calendarview.getSelectedDate().getMonth(), calendarview.getSelectedDate().getDay());
                    if (calendarDay.isAfter(calendarview.getSelectedDate())) {
                        localDate = localDate.plusDays(7);
                    } else if (calendarDay.isBefore(calendarview.getSelectedDate())) {
                        localDate = localDate.minusDays(7);
                    }
                    calendarview.setCurrentDate(localDate);
                    calendarview.setSelectedDate(localDate);
                    calendarview.setDateSelected(CalendarDay.from(localDate), true);
                    updateEvents(calendarview.getSelectedDate());
                }
            }
        });

        emptyActionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getAllSchedules();
            }
        });

        emptyActionBtn.setText(getString(R.string.retry));
    }

    private void setCalendarMode(CalendarMode mode) {

        if (calendarview.getCalendarMode() != mode) {
            calendarview
                    .state()
                    .edit()
                    .setCalendarDisplayMode(mode)
                    .isCacheCalendarPositionEnabled(true)
                    .setMinimumDate(CalendarDay.from(CalendarDay.today().getYear() - 2, 1, 1))
                    .setMaximumDate(CalendarDay.from(CalendarDay.today().getYear() + 1, 12, 31))
                    .commit();
        }

        calendarview.setCurrentDate(calendarview.getSelectedDate(), true);

        if (mode == CalendarMode.MONTHS) {
            isWeekScrollEnabled = false;
        } else {
            isWeekScrollEnabled = true;
        }
    }

    private void setEmptyState() {
        emptyIv.setImageDrawable(getActivity().getDrawable(EmptyStateUtil.getImage(EmptyViewConstants.EMPTY_APPOINTMENTS_WITH_BTN)));
        emptyTitleTv.setText(EmptyStateUtil.getTitle(getActivity(), EmptyViewConstants.EMPTY_APPOINTMENTS_WITH_BTN));
        emptyMessageTv.setText(EmptyStateUtil.getMessage(getActivity(), EmptyViewConstants.EMPTY_APPOINTMENTS_WITH_BTN));


        emptyActionBtn.setVisibility(View.GONE);

    }

    private void setNoNetworkEmptyState() {
        emptyIv.setImageDrawable(getActivity().getDrawable(EmptyStateUtil.getImage(EmptyViewConstants.EMPTY_NO_NETWORK)));
        emptyTitleTv.setText(EmptyStateUtil.getTitle(getActivity(), EmptyViewConstants.EMPTY_NO_NETWORK));
        emptyMessageTv.setText(EmptyStateUtil.getMessage(getActivity(), EmptyViewConstants.EMPTY_NO_NETWORK));

        emptyActionBtn.setVisibility(View.VISIBLE);
    }

    private void updateEvents(CalendarDay calendarDay) {
        Calendar calendar = getCalendarFromCalendarDay(calendarDay);

        weekView.goToDate(calendar);
        weekView.notifyDataSetChanged();

        updateView(calendar);
    }

    private Calendar getCalendarFromCalendarDay(CalendarDay calendarDay) {
        Calendar calendar = Calendar.getInstance();
        try {
            Date date = new SimpleDateFormat(Utils.yyyy_mm_dd).parse(calendarDay.getYear() + "-" + calendarDay.getMonth() + "-" + calendarDay.getDay());
            calendar.setTime(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return calendar;
    }

    private void setUpWeekView() {
        weekView.setOnEventClickListener(this);

        weekView.setMonthChangeListener(this);

        weekView.setDefaultEventColor(R.color.app_gradient_end);

        weekView.setDateTimeInterpreter(new DateTimeInterpreter() {
            SimpleDateFormat weekdayNameFormat = new SimpleDateFormat("EEE", Locale.getDefault());

            @Override
            public String interpretDate(Calendar date) {
                String weekday = weekdayNameFormat.format(date.getTime());
                if (weekView.getNumberOfVisibleDays() == 7) {
                    weekday = String.valueOf(weekday.charAt(0));
                }
                return weekday.toUpperCase();
            }

            @Override
            public String interpretTime(int hour) {
                String amPm;
                if (hour >= 12) {
                    amPm = "PM";
                } else {
                    amPm = "AM";
                }
                int hr;
                if (hour == 0 || hour == 24) {
                    hr = 12;
                } else if (hour > 12) {
                    hr = hour - 12;
                } else {
                    hr = hour;
                }
                return hr + " " + amPm;
            }
        });

        weekView.setVerticalScrollListener(new OnVerticalScrollListener() {
            @Override
            public void onScrollTop() {
                setCalendarMode(CalendarMode.WEEKS);
            }

            @Override
            public void onScrollDown() {
                setCalendarMode(CalendarMode.MONTHS);
            }
        });

        weekView.setScrollListener(this);

        weekView.setOverlappingEventGap(20);

        weekView.goToToday();

    }

    @Override
    public void onEventClick(Object data, RectF eventRect) {

        SchedulesApiResponseModel.ResultBean resultBean = (SchedulesApiResponseModel.ResultBean) data;

        ScheduleDetailViewFragment scheduleDetailViewFragment = new ScheduleDetailViewFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(ArgumentKeys.SCHEDULE_DETAIL, resultBean);
        scheduleDetailViewFragment.setArguments(bundle);
        showSubFragmentInterface.onShowFragment(scheduleDetailViewFragment);
    }

    @Override
    public List<WeekViewDisplayable> onMonthChange(Calendar startDate, Calendar endDate) {
        Calendar calendar = Calendar.getInstance();
        if (calendar.get(Calendar.MONTH) != endDate.get(Calendar.MONTH)) {
            return new ArrayList<>();
        } else {
            return weekViewEventList;
        }
    }


    private void getAllSchedules() {
        schedulesApiViewModel.getUserSchedules(null, doctorGuidList.toString(), false, true);
    }

    @Override
    public void onFirstVisibleDayChanged(Calendar newFirstVisibleDay, Calendar oldFirstVisibleDay) {
        currentDay = newFirstVisibleDay;
        if (calendarview.getSelectedDate() != null && newFirstVisibleDay.compareTo(getCalendarFromCalendarDay(calendarview.getSelectedDate())) != 0) {
            CalendarDay calendarDay = CalendarDay.from(newFirstVisibleDay.get(Calendar.YEAR), newFirstVisibleDay.get(Calendar.MONTH) + 1, newFirstVisibleDay.get(Calendar.DAY_OF_MONTH));
            calendarview.clearSelection();
            calendarview.setCurrentDate(calendarDay, true);
            calendarview.setDateSelected(calendarDay, true);

            updateView(newFirstVisibleDay);
        }
    }

    private void updateView(Calendar newFirstVisibleDay) {
        boolean isEventAvailable = false;
        for (int i = 0; i < weekViewEventList.size(); i++) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(weekViewEventList.get(i).toWeekViewEvent().getStartTime().getTime());
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            if (cal.getTime().compareTo(newFirstVisibleDay.getTime()) == 0) {
                isEventAvailable = true;
                weekView.goToHour(weekViewEventList.get(i).toWeekViewEvent().getStartTime().get(Calendar.HOUR_OF_DAY));
                break;
            }
        }
        if (isEventAvailable) {
            recyclerEmptyStateView.setVisibility(View.GONE);
        } else {
            if (recyclerEmptyStateView.getVisibility() == View.VISIBLE) {
                recyclerEmptyStateView.setVisibility(View.GONE);
                weekView.setVisibility(View.GONE);
            }
            int fadeInDuration = 500; // Configure time values here
            int timeBetween = 3000;
            int fadeOutDuration = 1000;

            AnimationSet animationSet = new AnimationSet(false);
            Animation fadeIn = new AlphaAnimation(0, 1);
            fadeIn.setInterpolator(new DecelerateInterpolator()); // add this
            fadeIn.setDuration(fadeInDuration);

            Animation fadeOut = new AlphaAnimation(1, 0);
            fadeOut.setInterpolator(new AccelerateInterpolator()); // and this
            fadeOut.setStartOffset(fadeInDuration + timeBetween);
            fadeOut.setDuration(fadeOutDuration);

            recyclerEmptyStateView.setAnimation(animationSet);
            recyclerEmptyStateView.animate()
                    .setDuration(300)
                    .setListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            recyclerEmptyStateView.setVisibility(View.VISIBLE);
                            weekView.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {

                        }

                        @Override
                        public void onAnimationRepeat(Animator animation) {

                        }
                    })
                    .start();
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        addFab.setClickable(true);
        if (getUserVisibleHint()) {
            setUserVisibleHint(true);
        }
        if (!Utils.isInternetEnabled(getActivity())) {
            setNoNetworkEmptyState();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        Utils.hideOverlay();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            makeApiCall();
        }
    }

    private void makeApiCall() {
        if (UserType.isUserAssistant()) {
            getDoctorsList();
        } else {
            getAllSchedules();
        }
    }

    private void getDoctorsList() {
        associationApiViewModel.getAssociationList(true, null);
    }

}
