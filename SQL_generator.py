import random
from collections import defaultdict
GENERATE_GRAPH = False;

# Put test cases here
accounts_list = { 
'acctA': ['customerA', 'customerC'] ,
'acctB': ['customerB'],
'acctC': ['customerC'],
'acctD': ['customerA', 'customerD'],
}
transfer_list = [ 
('acctA', 'acctB'),
('acctB', 'acctC'),
('acctB', 'acctD')
]

accounts_list = { 
'acctA': ['customerA'] ,
'acctB': ['customerB'],
'acctC': ['customerC'],
'acctD': ['customerD'],
'acctE': ['customerE'],

}
transfer_list = [ 
('acctA', 'acctB'),
('acctB', 'acctC'),
('acctC', 'acctD'),
('acctD', 'acctE')

]




####################################
def generateRandomGraph(numCustomers=10, numAccount=5, probabityC=0.1, probabiltyEdge=0.4 ):
    accounts_list = defaultdict(list)
    transfer_list = []
    custm_set = set()
    account_set = set()
    for i in range(1, numCustomers+1):
        custm_set.add(f'customer{i}')
    for i in range(1, numAccount+1):
        account_set.add(f'account{i}')
    
    for a in account_set:
        for c in custm_set:
            if(random.random() < probabityC):
                accounts_list[a].append(c)

    for a1 in accounts_list:
        for a2 in accounts_list:
            if(random.random() < probabiltyEdge):
                transfer_list.append((a1,a2))

    return accounts_list, transfer_list

def bfs(graph, node):
    visited = list() # List to keep track of visited nodes.
    queue = []     #Initialize a queue
    queue.append(node)

    while queue:
        s = queue.pop(0) 

        for neighbour in graph[s]:
            if neighbour not in visited:
                visited.append(neighbour)
                queue.append(neighbour)
    return visited
###################################


if(GENERATE_GRAPH):
    accounts_list, transfer_list = generateRandomGraph()

print("account_list" + str(accounts_list))
print("transfer_list" + str(accounts_list))


custm_set = set()
queries = []
depos_query = []
graph = defaultdict(list)
for account in accounts_list:
    customers = accounts_list[account]
    query = f"""INSERT INTO account VALUES('{account}', '{random.randint(0,1000000)}')"""
    queries.append(query)
    for c in customers:
        query = f"""INSERT INTO depositor VALUES('{c}', '{account}')"""
        depos_query.append(query)
        custm_set.add(c)

for c in custm_set:
    query = f"""INSERT INTO customer VALUES('{c}', '{random.randint(100,800)}')"""
    queries.append(query)

queries += depos_query

for src, tgt in transfer_list:
    query = f"""INSERT INTO transfer VALUES('{src}', '{tgt}', '2022-01-21', '{random.randint(1000,10000)}')"""
    queries.append(query)
    for c1 in accounts_list[src]:
        for c2 in accounts_list[tgt]:
            graph[c1].append(c2)
result = []
print("graph " + str(graph))

for c in custm_set:
    reachable = bfs( graph, c)
    for c2 in reachable:
        result.append((c, c2))
result.sort(key=lambda c1: (c1[0],c1[1])) 
f = open("queries.txt", "w")
for line in queries:
    f.write("%s\n" % line)
for a,b in result:
    f.write(f"""INSERT INTO influence_ans VALUES('{a}', '{b}')\n""")
f.truncate(f.tell()-2)
f.close()