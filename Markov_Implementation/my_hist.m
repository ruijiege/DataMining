function my_hist(M)
%% give a histogram of the size of cluster
%% for helping to choose the number of clusters

cnt = 0;
for i = 1:size(M,1)
    if nnz(M(i,:)) ~= 0
        cnt = cnt + 1;
        a(cnt) = nnz(M(i,:));
    end
end

figure;
X = 1:length(a);
Y = sort(a);
bar(X,Y)